using System;
using System.Data.SqlClient;
using System.Collections.Generic;

namespace Palletization {
    class Program {
        private readonly static String URL = "Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;";

        static void Main(string[] args) {
            Palletize(0);       //FIXME: Read from command line or something
            Console.WriteLine("Press any key to exit...");
            Console.ReadKey();
        }

        //Creates pallets for the given route number
        static void Palletize(int routeID) {
            using (SqlConnection connection = new SqlConnection(URL)) {
                List<PalletizeOrder> orders = getOrders(routeID, connection);
                orders.Sort();
                foreach (PalletizeOrder order in orders)
                    foreach (PalletizeProduct product in order.products)
                        Console.WriteLine("Order #" + order.orderNum + " at stop #" + order.stopNum + " contains " + product.quantity + " of product #" + product.prodID + " of priority #" + product.priority + " and volume " + product.volume + "cm^3");

                List<PalletizePallet> pallets = LenientPalletize(orders);

                if (pallets.Count > PalletizePallet.MAX_NUM_PALLETS) {                         // Attempt to combine pallets
                    // FIXME
                }

                if (pallets.Count > PalletizePallet.MAX_NUM_PALLETS) {                         // Use less lenient palletization
                    pallets = StrictPalletize(orders);
                }

                // FIXME: Add to pallet table. Should change palletIDs to start from the ID + 1 of the last ID in the table.
            }
        }

        // Get all orders on route
        private static List<PalletizeOrder> getOrders(int routeID, SqlConnection connection) {
            connection.Open();

            List<int> orderNums = new List<int>();
            List<int> stopNums = new List<int>();

            SqlCommand getRoute = new SqlCommand("select * from route_info where route_id = " + routeID + ";", connection);
            using (SqlDataReader routesReader = getRoute.ExecuteReader()) {
                for (int i = 0; routesReader.Read(); i++) {
                    orderNums.Add(routesReader.GetInt32(routesReader.GetOrdinal("order_num")));
                    stopNums.Add(routesReader.GetInt32(routesReader.GetOrdinal("stop_num")));
                }
                routesReader.Close();
            }

            List<PalletizeOrder> orders = new List<PalletizeOrder>();

            for (int i = 0; i < orderNums.Count; i++) {
                PalletizeOrder order = new PalletizeOrder(orderNums[i], stopNums[i]);
                SqlCommand getProduct = new SqlCommand("select order_item.prod_id as prod_id, volume, proirity as priority, quantity from order_full, order_item, product where product.prod_id = order_item.prod_id and order_full.order_num = order_item.order_num and order_full.order_num = " + orderNums[i] + ";", connection);
                using (SqlDataReader productsReader = getProduct.ExecuteReader()) {
                    while (productsReader.Read()) {
                        PalletizeProduct product = new PalletizeProduct();
                        product.prodID = productsReader.GetInt32(productsReader.GetOrdinal("prod_id"));
                        product.volume = productsReader.GetFloat(productsReader.GetOrdinal("volume"));
                        product.priority = productsReader.GetInt32(productsReader.GetOrdinal("priority"));
                        product.quantity = productsReader.GetInt32(productsReader.GetOrdinal("quantity"));
                        order.products.Add(product);
                    }
                    productsReader.Close();
                }
                orders.Add(order);
            }

            connection.Close();
            return orders;
        }

        private static List<PalletizePallet> LenientPalletize(List<PalletizeOrder> orders) {
            List<PalletizePallet> pallets = new List<PalletizePallet>();
            int palletID = 0;
            pallets.Add(new PalletizePallet(0));

            foreach (PalletizeOrder order in orders) {
                float totalVolume = 0f;
                foreach (PalletizeProduct product in order.products) totalVolume += product.volume * product.quantity;
                if (totalVolume > PalletizePallet.MAX_PALLET_VOLUME) {                          // Split order since larger than any pallet
                    PalletizeOrder splitOrder = new PalletizeOrder(order.orderNum, order.stopNum);
                    foreach (PalletizeProduct product in order.products) {
                        if (product.volume * product.quantity > pallets[palletID].volumeRemaining) {    // Select next pallet since full
                            pallets[palletID].orders.Add(splitOrder);
                            pallets.Add(new PalletizePallet(++palletID));
                            splitOrder = new PalletizeOrder(order.orderNum, order.stopNum);
                        }
                        splitOrder.products.Add(product);                                               // Add products to current pallet
                        pallets[palletID].volumeRemaining -= product.volume * product.quantity;
                    }
                    pallets[palletID].orders.Add(splitOrder);
                } else {
                    if (totalVolume > pallets[palletID].volumeRemaining)                   // Select next pallet since full
                        pallets.Add(new PalletizePallet(++palletID));
                    pallets[palletID].orders.Add(order);                                   // Add order to current pallet
                    pallets[palletID].volumeRemaining -= totalVolume;
                }
            }

            return pallets;
        }

        private static List<PalletizePallet> StrictPalletize(List<PalletizeOrder> orders) {            // FIXME
            return null;
        }
    }

    class PalletizeOrder : IComparable<PalletizeOrder> {
        public int orderNum;
        public int stopNum;
        public List<PalletizeProduct> products;

        public PalletizeOrder(int orderNum, int stopNum) {
            this.orderNum = orderNum;
            this.stopNum = stopNum;
            products = new List<PalletizeProduct>();
        }

        public int CompareTo(PalletizeOrder that) {
            return that.stopNum - this.stopNum;
        }
    }
    class PalletizeProduct {
        public int prodID;
        public float volume;
        public int priority;
        public int quantity;
    }
    class PalletizePallet {
        // Source: https://pe.usps.com/qsg_archive/PDF/QSG_Archive_20060108/qsg300/q705b.pdf
        public readonly static float MAX_PALLET_VOLUME = 375513.6f;    //cm^3
        public readonly static int MAX_NUM_PALLETS = 24;

        public int palletID;
        public float volumeRemaining;
        public List<PalletizeOrder> orders;

        public PalletizePallet(int palletID) {
            this.palletID = palletID;
            volumeRemaining = MAX_PALLET_VOLUME;
            orders = new List<PalletizeOrder>();
        }
    }
}
