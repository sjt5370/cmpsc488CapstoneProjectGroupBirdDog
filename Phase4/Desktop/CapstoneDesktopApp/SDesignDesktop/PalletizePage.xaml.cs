using System;
using System.Data;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace SDesignDesktop
{
    /// <summary>
    /// Interaction logic for PalletizePage.xaml
    /// </summary>
    public partial class PalletizePage : Page
    {
        public PalletizePage()
        {
            InitializeComponent();
            this.updateGUI();
            this.UpdateLayout();

        }
        
        System.Windows.Controls.ListBoxItem route = new ListBoxItem();
        private string routeSelect;
        private string palNum;
        private void navBack_Click(object sender, RoutedEventArgs e)
        {
            SDesignDesktop.Main.GetWindow(this).Content = new ViewPage();

        }
       
        private void selectRoute_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            routeSelect = ((sender as ComboBox).SelectedItem as ComboBoxItem).Content as string;
        }

        private void sRoute_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            //palNum = Convert.ToInt32(((sender as ComboBox).SelectedItem as ComboBoxItem).Content);
            palNum = ((sender as ComboBox).SelectedItem as ComboBoxItem).Content as string;
        }

        //private void Page_Loaded(object sender, RoutedEventArgs e)
       // {
           
        //}
        static private int globalRoute;
        static private int numCenterPoints;
        private void updateGUI()
        {

            SqlConnection con2 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (con2)
            {
                con2.Open();
                SqlCommand getNumberOfRoutes = new SqlCommand("SELECT COUNT(DISTINCT route_id) FROM route_info", con2);
                SqlDataReader numRts = getNumberOfRoutes.ExecuteReader();
                numRts.Read();
                numCenterPoints = numRts.GetInt32(0);
                con2.Close();
                numRts.Close();
            }
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection)
            {
                int count = numCenterPoints + 1;
                //RouteList.Items.Clear();
                for (int i = 1; i < count; i++)
                {
                    connection.Open();
                    int routenum = 100 + i;
                    System.Windows.Controls.ComboBoxItem addCbox = new ComboBoxItem();
                    System.Windows.Controls.ListBoxItem routeTitle = new ListBoxItem();

                    SqlCommand palletized = new SqlCommand("SELECT DISTINCT route_id FROM route_info EXCEPT SELECT route_id FROM pallet", connection);
                    SqlDataReader incRead = palletized.ExecuteReader();
                    int routes;
                    while (incRead.Read())
                    {
                        routes = incRead.GetInt32(0);
                        if (routes == routenum)
                        {
                            addCbox.Content = "Route" + Convert.ToString(routenum);
                            selectRoute.Items.Add(addCbox);
                        }
                    }
                    incRead.Close();
                    routeTitle.Content = "Route " + routenum;
                    routeTitle.FontSize = 20;//this is the listener that watches for clicking on route and passes the route number for querying the pallets of the route
                    routeTitle.Selected += delegate (object sender, RoutedEventArgs e) { RouteTitle_Selected(sender, e, routenum); };
                    //RouteTitle_Selected;
                    //delegate (object sender, MouseButtonEventArgs e) { RouteList_MouseDoubleClick(sender, e, routenum); };
                    routeTitle.SetValue(ListBoxItem.FontWeightProperty, FontWeights.Bold);
                    route = new ListBoxItem();
                    System.Windows.Controls.ListBox orders = new ListBox();
                    orders.FontSize = 12;
                    orders.Height = 140;
                    orders.Width = 390;
                    orders.BorderBrush = Brushes.Black;
                    orders.BorderThickness.Equals(2);
                    route.Height = 150;
                    route.Width = 400;
                    route.SetValue(ListBoxItem.HorizontalAlignmentProperty, HorizontalAlignment.Center);
                    route.BorderThickness.Equals(1);
                    RouteList.Items.Add(routeTitle);
                    connection.Close();
                    connection.Open();
                    SqlCommand getRoutes = new SqlCommand("SELECT * FROM routelist WHERE route_id = " + routenum + ";", connection);
                    SqlDataReader readRoutes = getRoutes.ExecuteReader();

                    while (readRoutes.Read())
                    {
                        System.Windows.Controls.ListBoxItem edit = new ListBoxItem();
                        edit.SetValue(ListBoxItem.HorizontalContentAlignmentProperty, HorizontalAlignment.Center);

                        System.Windows.Controls.StackPanel stk = new StackPanel();
                        stk.SetValue(StackPanel.OrientationProperty, Orientation.Horizontal); stk.SetValue(StackPanel.HorizontalAlignmentProperty, HorizontalAlignment.Left);

                        System.Windows.Controls.TextBlock account = new TextBlock();
                        account.Width = 80;
                        account.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                        account.Text = Convert.ToString(readRoutes.GetInt32(0));


                        System.Windows.Controls.TextBlock name = new TextBlock();
                        name.Width = 250;
                        name.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left); name.Text = readRoutes.GetString(1);


                        System.Windows.Controls.TextBlock totalbox = new TextBlock();
                        totalbox.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Right);
                        totalbox.Width = 30;
                        SqlConnection connection2 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
                        using (connection2)
                        {
                            connection2.Open();
                            SqlCommand getTotals = new SqlCommand("SELECT SUM(quantity) FROM orders WHERE acc_id    = " + readRoutes.GetInt32(0) + ";", connection2);
                            string tot = Convert.ToString(getTotals.ExecuteScalar());
                            totalbox.Text = tot;



                            stk.Children.Add(account);
                            stk.Children.Add(name);
                            stk.Children.Add(totalbox);
                            edit.Content = stk;
                            edit.Selected += delegate (object se, RoutedEventArgs es) { order1_MouseDoubleClick(se, es, Convert.ToInt32(account.Text)); };
                            orders.Items.Add(edit);
                        }
                        connection2.Close();
                    }
                    route.Content = orders;
                    RouteList.Items.Add(route);
                    connection.Close();
                   
                }
            }
        }
        int index;
        private void RouteTitle_Selected(object sender, RoutedEventArgs e, int RouteNumber)
        {
            
            globalRoute = RouteNumber;
            PalletList.Items.Clear();
            SqlConnection connection4 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            SqlConnection connection5 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection4)
            {
                //sRoute.Items.Insert(0, default2);
                connection4.Open();
                SqlCommand getCount = new SqlCommand("SELECT COUNT(DISTINCT pallet_id) FROM getPallet_Info WHERE route_id = " + RouteNumber, connection4);
                int count = Convert.ToInt32(getCount.ExecuteScalar());
                //sRoute.Items.Clear();
                System.Windows.Controls.ComboBox tempBox = new ComboBox();
                System.Windows.Controls.ComboBoxItem placeholder = new ComboBoxItem();
                placeholder.Content = "#";
                tempBox.Items.Add(placeholder);
                tempBox.SelectedIndex = 0;
                tempBox.SelectionChanged += sRoute_SelectionChanged;
                tempBox.Width = 60;
                tempBox.Height = 49;
                tempBox.MaxDropDownHeight = 100;
                //tempBox.SetValue(ComboBox.SelectedValuePathProperty, "Content");
                tempBox.SetValue(ComboBox.HorizontalContentAlignmentProperty, HorizontalAlignment.Center);
                //tempBox.SelectedValuePath = "Content";
                
                
                for (int i = 0; i < count; i++)
                {
                    sRoute = tempBox;
                    int palletNum = i + 1;
                    System.Windows.Controls.ComboBoxItem palID = new ComboBoxItem();
                    palID.Content = palletNum.ToString();
                    sRoute.Items.Add(palID);
                    connection5.Open();
                    System.Windows.Controls.TextBlock palletNumTitle = new TextBlock();
                    System.Windows.Controls.ListBoxItem title1 = new ListBoxItem();
                    palletNumTitle.Text = "Pallet " + palletNum.ToString();
                    palletNumTitle.FontSize = 30;
                    palletNumTitle.SetValue(TextBlock.HorizontalAlignmentProperty, HorizontalAlignment.Center);
                    title1.Content = palletNumTitle;
                    PalletList.Items.Add(title1);
                    System.Windows.Controls.ListBoxItem innerBoxHolder = new ListBoxItem();
                    System.Windows.Controls.ListBox innerBox = new ListBox();
                    innerBox.Name = "pallet" + i.ToString();
                    innerBox.FontSize = 12;
                    innerBox.Height = 200;
                    innerBox.Width = 500;
                    SqlCommand getPallets = new SqlCommand("SELECT quantity, prod_name, cus_name, prod_id, order_num FROM getPallet_Info WHERE route_id = " + RouteNumber + " AND pallet_id = " + palletNum, connection5);
                    SqlDataReader grabPallets = getPallets.ExecuteReader();
                    index = 1;
                    while (grabPallets.Read())
                    {
                        System.Windows.Controls.ComboBoxItem palletListItem = new ComboBoxItem();
                        palletListItem.Content = index;
                        //sRoute.Items.Add(palletListItem);
                        object[] productList = new object[5];
                        grabPallets.GetValues(productList);
                        System.Windows.Controls.ListBoxItem palletItems = new ListBoxItem();
                        palletItems.SetValue(ListBoxItem.HorizontalContentAlignmentProperty, HorizontalAlignment.Center);

                        System.Windows.Controls.StackPanel stkpnl = new StackPanel();
                        stkpnl.SetValue(StackPanel.OrientationProperty, Orientation.Horizontal); stkpnl.SetValue(StackPanel.HorizontalAlignmentProperty, HorizontalAlignment.Left);

                        System.Windows.Controls.TextBlock quantity = new TextBlock();
                        quantity.Width = 100;
                        quantity.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                        quantity.Text = Convert.ToString(productList[0]);

                        System.Windows.Controls.TextBlock prodName = new TextBlock();
                        prodName.Width = 200;
                        prodName.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                        prodName.Text = Convert.ToString(productList[1]);

                        System.Windows.Controls.TextBlock cusName = new TextBlock();
                        cusName.Width = 200;
                        cusName.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                        cusName.Text = Convert.ToString(productList[2]);

                        stkpnl.Children.Add(quantity);
                        stkpnl.Children.Add(prodName);
                        stkpnl.Children.Add(cusName);
                        palletItems.Content = stkpnl;
                        palletItems.Selected += delegate (object sender1, RoutedEventArgs e1) { PalletItems_Selected(sender1, e, palletNum, Convert.ToInt32(productList[3]), Convert.ToInt32(productList[4]), Convert.ToInt32(quantity.Text), prodName.Text, cusName.Text); };
                        innerBox.Items.Add(palletItems);
                        index++;
                    }
                    innerBoxHolder.Content = innerBox;
                    PalletList.Items.Add(innerBoxHolder);
                    connection5.Close();
                }
                connection4.Close();
            }
            //throw new NotImplementedException();
        }

        /////////////////////////////////////////////////////////////////////////////////////
        // query the db and display results in the edit window as the pallets             //
        /////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        // In this method we pass the information after clicking on an item in the pallet list                 //
        /////////////////////////////////////////////////////////////////////////////////////////////////////////
        private static List<int> SelectedPalletNum = new List<int>();
        private static List<int> SelectedProdId = new List<int>();
        private static List<int> SelectedOrderNum = new List<int>();
        private static List<int> SelectedQuantity = new List<int>();
        private static List<string> SelectedprodName = new List<string>();
        private static List<string> SelectedcusName = new List<string>();
        private void PalletItems_Selected(object sender1, RoutedEventArgs e1, int palletNum, int prod_id , int order_num, int quantity, string prodName, string cusName)
        {
            SelectedPalletNum.Add(palletNum);
            SelectedProdId.Add(prod_id);
            SelectedOrderNum.Add(order_num);
            SelectedQuantity.Add(quantity);
            SelectedprodName.Add(prodName);
            SelectedcusName.Add(cusName);

            object[] productList = new object[5];
            System.Windows.Controls.ListBoxItem palletItems = new ListBoxItem();
            palletItems.SetValue(ListBoxItem.HorizontalContentAlignmentProperty, HorizontalAlignment.Center);

            System.Windows.Controls.StackPanel stkpnl = new StackPanel();
            stkpnl.Width = 475;
            stkpnl.SetValue(StackPanel.OrientationProperty, Orientation.Horizontal); stkpnl.SetValue(StackPanel.HorizontalAlignmentProperty, HorizontalAlignment.Left);

            System.Windows.Controls.TextBlock quantity1 = new TextBlock();
            quantity1.Width = 80;
            quantity1.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
            quantity1.Text = Convert.ToString(productList[0]);

            System.Windows.Controls.TextBlock prodName1 = new TextBlock();
            prodName1.Width = 180;
            prodName1.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
            prodName1.Text = Convert.ToString(productList[1]);

            System.Windows.Controls.TextBlock cusName1 = new TextBlock();
            cusName1.Width = 180;
            cusName1.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
            cusName1.Text = Convert.ToString(productList[2]);

            stkpnl.Children.Add(quantity1);
            stkpnl.Children.Add(prodName1);
            stkpnl.Children.Add(cusName1);
            palletItems.Content = stkpnl;
            Selected.Items.Add(palletItems);
        }


        private int tempID;

        public void order1_MouseDoubleClick(object se, RoutedEventArgs es, int accnum)
        {
            tempID = accnum;

            //textBox.Text = Convert.ToString(accnum);
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");

            using (connection)
            {
                connection.Open();


                SqlCommand getRoute = new SqlCommand("SELECT route_id FROM routelist WHERE acc_id = " + accnum + ";", connection);
                //tempRoute = Convert.ToInt32(getRoute.ExecuteScalar());
                SqlCommand getProducts = new SqlCommand("SELECT prod_name, quantity FROM orders WHERE acc_id = " + accnum + ";", connection);
                using (getProducts)
                {
                    SqlDataReader reader = getProducts.ExecuteReader();
                    using (reader)
                    {
                        if (listBox.Items.Count != 0)
                        {
                            listBox.Items.Clear();
                        }
                        while (reader.Read())
                        {
                            System.Windows.Controls.ListBoxItem edit = new ListBoxItem();
                            System.Windows.Controls.StackPanel stk = new StackPanel();
                            stk.SetValue(StackPanel.OrientationProperty, Orientation.Horizontal);
                            stk.SetValue(StackPanel.HorizontalAlignmentProperty, HorizontalAlignment.Left);
                            edit.SetValue(ListBoxItem.HorizontalContentAlignmentProperty, HorizontalAlignment.Left);
                            System.Windows.Controls.TextBlock prod = new TextBlock();
                            prod.Width = 370;
                            prod.FontSize = 12;
                            prod.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                            prod.Text = reader.GetString(0);
                            System.Windows.Controls.TextBlock quant = new TextBlock();
                            quant.Width = 80;
                            quant.FontSize = 12;
                            quant.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Right);
                            quant.Text = Convert.ToString(reader.GetInt32(1));
                            stk.Children.Add(prod);
                            stk.Children.Add(quant);
                            edit.Content = stk;
                            listBox.Items.Add(edit);
                        }
                        reader.Close();
                    }
                }
            }
            connection.Close();
        }
        public static int NumPallets;
        private void Palletize_Click(object sender, RoutedEventArgs e)
        {
            //selectRoute.Items.RemoveAt(0);
            NumPallets = Convert.ToInt32(numRoutes.Text);
            string route = routeSelect;
            int RouteNum = Convert.ToInt32(route.Substring(5));
            Palletize(RouteNum);
            int selected = selectRoute.SelectedIndex;
            selectRoute.SelectedItem = default1;
            selectRoute.Items.RemoveAt(selected);
            
        }
            //static void Main(string[] args)
            //{
            //    Palletize(0);       // Replace 0 with route number to generate pallets for
                //Console.WriteLine("Press any key to exit...");
                //Console.ReadKey();
           // }
            //private readonly static String URL = "Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;";
        //Creates pallets for the given route number
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            public static void Palletize(int routeID)
            {
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection)
                {
                connection.Open();
                    List<PalletizeOrder> orders = getOrders(routeID, connection);
                    orders.Sort();
                    foreach (PalletizeOrder order in orders)
                        foreach (PalletizeProduct product in order.products)
                            Console.WriteLine("Order #" + order.orderNum + " at stop #" + order.stopNum + " contains " + product.quantity + " of product #" + product.prodID + " of priority #" + product.priority + " and volume " + product.volume + "cm^3");

                    //List<PalletizePallet> pallets = LenientPalletize(orders);                       // Attempt to keep individual orders together
                    //if (pallets == null) pallets = ModeratePalletize(orders);
                    List<PalletizePallet> pallets = ModeratePalletize(orders);

                    if (pallets.Count > PalletizePallet.MAX_NUM_PALLETS)                            // Attempt to combine pallets
                        for (int i = 0; i < pallets.Count; i++)
                            for (int j = i + 1; j < pallets.Count; j++)
                                if (pallets[i].volumeRemaining - (PalletizePallet.MAX_PALLET_VOLUME - pallets[j].volumeRemaining) >= 0f)
                                {
                                    pallets[i].orders.AddRange(pallets[j].orders);
                                    pallets[i].volumeRemaining -= PalletizePallet.MAX_PALLET_VOLUME - pallets[j].volumeRemaining;
                                    pallets.RemoveAt(j--);
                                }

                    if (pallets.Count > PalletizePallet.MAX_NUM_PALLETS)                         // Palletize without regard for the splitting of orders
                        pallets = StrictPalletize(orders);

                    AddPallets(pallets, routeID, connection);
                
                }
            
            }
            
            // Get all orders on route
            private static List<PalletizeOrder> getOrders(int routeID, SqlConnection connection)
            {
            SqlConnection connection2 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection2)
            {
                connection2.Open();

                List<int> orderNums = new List<int>();
                List<int> stopNums = new List<int>();

                SqlCommand getRoute = new SqlCommand("select * from route_info where route_id = " + routeID + ";", connection2);
                using (SqlDataReader routesReader = getRoute.ExecuteReader())
                {
                    for (int i = 0; routesReader.Read(); i++)
                    {
                        orderNums.Add(routesReader.GetInt32(routesReader.GetOrdinal("order_num")));
                        stopNums.Add(routesReader.GetInt32(routesReader.GetOrdinal("stop_num")));
                    }
                    routesReader.Close();
                }

                List<PalletizeOrder> orders = new List<PalletizeOrder>();

                for (int i = 0; i < orderNums.Count; i++)
                {
                    PalletizeOrder order = new PalletizeOrder(orderNums[i], stopNums[i]);
                    SqlCommand getProduct = new SqlCommand("select order_item.prod_id as prod_id, volume, proirity as priority, quantity from order_full, order_item, product where product.prod_id = order_item.prod_id and order_full.order_num = order_item.order_num and order_full.order_num = " + orderNums[i] + ";", connection2);
                    using (SqlDataReader productsReader = getProduct.ExecuteReader())
                    {
                        while (productsReader.Read())
                        {
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

                connection2.Close();
                return orders;
            }
            }

            // Palletize: Only split orders that cannot fit on a single pallet. Don't split a bulk of products in the same order.
            private static List<PalletizePallet> LenientPalletize(List<PalletizeOrder> orders)
            {
                List<PalletizePallet> pallets = new List<PalletizePallet>();
                int palletID = 0;
                pallets.Add(new PalletizePallet(0));

                foreach (PalletizeOrder order in orders)
                {
                    float totalVolume = 0f;
                    foreach (PalletizeProduct product in order.products) totalVolume += product.volume * product.quantity;
                    if (totalVolume > PalletizePallet.MAX_PALLET_VOLUME)
                    {                          // Split order since larger than any pallet
                        PalletizeOrder splitOrder = new PalletizeOrder(order.orderNum, order.stopNum);
                        foreach (PalletizeProduct product in order.products)
                        {
                            if (product.volume * product.quantity > PalletizePallet.MAX_PALLET_VOLUME) return null;
                            if (product.volume * product.quantity > pallets[palletID].volumeRemaining)
                            {    // Select next pallet since full
                                if (splitOrder.products.Count > 0) pallets[palletID].orders.Add(splitOrder);
                                pallets.Add(new PalletizePallet(++palletID));
                                splitOrder = new PalletizeOrder(order.orderNum, order.stopNum);
                            }
                            splitOrder.products.Add(product);                                               // Add products to current pallet
                            pallets[palletID].volumeRemaining -= product.volume * product.quantity;
                        }
                        if (splitOrder.products.Count > 0) pallets[palletID].orders.Add(splitOrder);
                    }
                    else
                    {
                        if (totalVolume > pallets[palletID].volumeRemaining)                   // Select next pallet since full
                            pallets.Add(new PalletizePallet(++palletID));
                        pallets[palletID].orders.Add(order);                                   // Add order to current pallet
                        pallets[palletID].volumeRemaining -= totalVolume;
                    }
                }

                return pallets;
            }

            // Palletize: Only split orders that cannot fit on a single pallet. The bulk of products of these orders can also be split.
            private static List<PalletizePallet> ModeratePalletize(List<PalletizeOrder> orders)
            {
                List<PalletizePallet> pallets = new List<PalletizePallet>();
                int palletID = 0;
                pallets.Add(new PalletizePallet(0));

                foreach (PalletizeOrder order in orders)
                {
                    float totalVolume = 0f;
                    foreach (PalletizeProduct product in order.products) totalVolume += product.volume * product.quantity;
                    if (totalVolume > PalletizePallet.MAX_PALLET_VOLUME)
                    {                          // Split order since larger than any pallet
                        PalletizeOrder splitOrder = new PalletizeOrder(order.orderNum, order.stopNum);
                        foreach (PalletizeProduct product in order.products)
                        {
                            if (product.volume * product.quantity <= pallets[palletID].volumeRemaining)
                            {   // Add all of product quantity if it fits
                                splitOrder.products.Add(product);
                                pallets[palletID].volumeRemaining -= product.volume * product.quantity;
                            }
                            else
                                        {                                                                        // Otherwise split quantity
                                PalletizeProduct splitProduct = new PalletizeProduct();
                                splitProduct.prodID = product.prodID;
                                splitProduct.volume = product.volume;
                                splitProduct.priority = product.priority;
                                splitProduct.quantity = 0;
                                for (int i = 0; i < product.quantity; i++)
                                {
                                    if (product.volume > pallets[palletID].volumeRemaining)
                                    {                   // Next pallet if no more room
                                        if (splitProduct.quantity > 0) splitOrder.products.Add(splitProduct);
                                        if (splitOrder.products.Count > 0) pallets[palletID].orders.Add(splitOrder);
                                        pallets.Add(new PalletizePallet(++palletID));
                                        splitOrder = new PalletizeOrder(order.orderNum, order.stopNum);
                                        splitProduct = new PalletizeProduct();
                                        splitProduct.prodID = product.prodID;
                                        splitProduct.volume = product.volume;
                                        splitProduct.priority = product.priority;
                                        splitProduct.quantity = 0;
                                    }
                                    splitProduct.quantity++;                                                    // Add a unit of product to pallet
                                    pallets[palletID].volumeRemaining -= product.volume;
                                }
                                if (splitProduct.quantity > 0) splitOrder.products.Add(splitProduct);
                            }
                        }
                        if (splitOrder.products.Count > 0) pallets[palletID].orders.Add(splitOrder);
                    }
                    else
                    {
                        if (totalVolume > pallets[palletID].volumeRemaining)                   // Select next pallet since full
                            pallets.Add(new PalletizePallet(++palletID));
                        pallets[palletID].orders.Add(order);                                   // Add order to current pallet
                        pallets[palletID].volumeRemaining -= totalVolume;
                    }
                }

                return pallets;
            }

            // Palletize: Split any order or bulk of products to fill up pallets as much as possible before selecting a new one
            private static List<PalletizePallet> StrictPalletize(List<PalletizeOrder> orders)
            {
                List<PalletizePallet> pallets = new List<PalletizePallet>();
                int palletID = 0;
                pallets.Add(new PalletizePallet(0));

                foreach (PalletizeOrder order in orders)
                {
                    float totalVolume = 0f;
                    foreach (PalletizeProduct product in order.products) totalVolume += product.volume * product.quantity;
                    if (totalVolume <= pallets[palletID].volumeRemaining)
                    {      // Add entire order if it fits
                        pallets[palletID].orders.Add(order);
                        pallets[palletID].volumeRemaining -= totalVolume;
                    }
                    else
                    {                                                    // Otherwise split it
                        PalletizeOrder splitOrder = new PalletizeOrder(order.orderNum, order.stopNum);
                        foreach (PalletizeProduct product in order.products)
                        {
                            if (product.volume * product.quantity <= pallets[palletID].volumeRemaining)
                            {   // Add all of product quantity if it fits
                                splitOrder.products.Add(product);
                                pallets[palletID].volumeRemaining -= product.volume * product.quantity;
                            }
                            else
                            {                                                                        // Otherwise split quantity
                                PalletizeProduct splitProduct = new PalletizeProduct();
                                splitProduct.prodID = product.prodID;
                                splitProduct.volume = product.volume;
                                splitProduct.priority = product.priority;
                                splitProduct.quantity = 0;
                                for (int i = 0; i < product.quantity; i++)
                                {
                                    if (product.volume > pallets[palletID].volumeRemaining)
                                    {                   // Next pallet if no more room
                                        if (splitProduct.quantity > 0) splitOrder.products.Add(splitProduct);
                                        if (splitOrder.products.Count > 0) pallets[palletID].orders.Add(splitOrder);
                                        pallets.Add(new PalletizePallet(++palletID));
                                        splitOrder = new PalletizeOrder(order.orderNum, order.stopNum);
                                        splitProduct = new PalletizeProduct();
                                        splitProduct.prodID = product.prodID;
                                        splitProduct.volume = product.volume;
                                        splitProduct.priority = product.priority;
                                        splitProduct.quantity = 0;
                                    }
                                    splitProduct.quantity++;                                                    // Add a unit of product to pallet
                                    pallets[palletID].volumeRemaining -= product.volume;
                                }
                                if (splitProduct.quantity > 0) splitOrder.products.Add(splitProduct);
                            }
                        }
                        if (splitOrder.products.Count > 0) pallets[palletID].orders.Add(splitOrder);
                    }
                }

                return pallets;
            }

            // Add to pallet table
            private static void AddPallets(List<PalletizePallet> pallets, int routeID, SqlConnection connection)
            {
            SqlConnection connection3 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection3)
            {
                int palletID = 0;
                connection3.Open();
                //SqlCommand getLastPalletID = new SqlCommand("select max(pallet_id) from pallet;", connection3);
                //using (SqlDataReader lastPalletIDReader = getLastPalletID.ExecuteReader())
                //{
               //     if (lastPalletIDReader.Read() && !lastPalletIDReader.IsDBNull(0))
               //         palletID = lastPalletIDReader.GetInt32(0);
               //     lastPalletIDReader.Close();
                //}
                foreach (PalletizePallet pallet in pallets)
                {
                    palletID++;
                    foreach (PalletizeOrder order in pallet.orders)
                    {
                        foreach (PalletizeProduct product in order.products)
                        {
                            String command = "insert into pallet values (" + palletID + ", " + product.prodID + ", " + routeID + ", " + order.orderNum + ", " + product.quantity + ", 0, 0);";
                            Console.WriteLine(command);
                            SqlCommand addPallet = new SqlCommand(command, connection3);
                            addPallet.ExecuteNonQuery();
                        }
                    }
                }
                connection3.Close();
            }
            }

        private void numUp_Click(object sender, RoutedEventArgs e)
        {
            numRoutes.Text = Convert.ToString(Convert.ToInt32(numRoutes.Text) + 1);
        }

        private void numDown_Click(object sender, RoutedEventArgs e)
        {
            numRoutes.Text = Convert.ToString(Convert.ToInt32(numRoutes.Text) - 1);
        }
        public int numPallets;

        private void remove_Click(object sender, RoutedEventArgs e)
        {
            int index = Selected.SelectedIndex;
            Selected.Items.RemoveAt(index);
            SelectedOrderNum.RemoveAt(index);
            SelectedPalletNum.RemoveAt(index);
            SelectedProdId.RemoveAt(index);
            SelectedcusName.RemoveAt(index);
            SelectedprodName.RemoveAt(index);
            SelectedQuantity.RemoveAt(index);
        }

        private void Clear_Click(object sender, RoutedEventArgs e)
        {
            Selected.Items.Clear();
            SelectedOrderNum.Clear();
            SelectedPalletNum.Clear();
            SelectedProdId.Clear();
            SelectedcusName.Clear();
            SelectedprodName.Clear();
            SelectedQuantity.Clear();
        }
        //private static int palletCount;
        private void moveTo_Click(object sender, RoutedEventArgs e)
        {
            string toPallet = sRoute.SelectedValue.ToString();
            int palletNumber = Convert.ToInt32(toPallet);
            int count = SelectedPalletNum.Count();
            SqlConnection connection8 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection8) {

                for (int i = 0; i < count; i++)
                {
                    connection8.Open();
                    SqlCommand updatePallets = new SqlCommand("UPDATE pallet SET pallet_id = " + palletNumber + " FROM pallet WHERE pallet_id = " + SelectedPalletNum[i] + " AND prod_id = " + SelectedProdId[i] + " AND order_num = " + SelectedOrderNum[i] + ";", connection8);
                    updatePallets.ExecuteNonQuery();
                    Selected.Items.RemoveAt(0);
                }
                SelectedOrderNum.Clear();
                SelectedPalletNum.Clear();
                SelectedProdId.Clear();
                SelectedcusName.Clear();
                SelectedprodName.Clear();
                SelectedQuantity.Clear();
                connection8.Close();
            }
            PalletList.Items.Clear();
            SqlConnection connection4 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            SqlConnection connection5 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection4)
            {
                connection4.Open();
                SqlCommand getCount = new SqlCommand("SELECT COUNT(DISTINCT pallet_id) FROM getPallet_Info WHERE route_id = " + globalRoute, connection4);
                int countNew = Convert.ToInt32(getCount.ExecuteScalar());

                for (int i = 0; i < countNew; i++)
                {
                    int palletNum = i + 1;
                    connection5.Open();
                    System.Windows.Controls.TextBlock palletNumTitle = new TextBlock();
                    System.Windows.Controls.ListBoxItem title1 = new ListBoxItem();
                    palletNumTitle.Text = "Pallet " + palletNum.ToString();
                    palletNumTitle.FontSize = 30;
                    palletNumTitle.SetValue(TextBlock.HorizontalAlignmentProperty, HorizontalAlignment.Center);
                    title1.Content = palletNumTitle;
                    PalletList.Items.Add(title1);
                    System.Windows.Controls.ListBoxItem innerBoxHolder = new ListBoxItem();
                    System.Windows.Controls.ListBox innerBox = new ListBox();
                    innerBox.Name = "pallet" + i.ToString();
                    innerBox.FontSize = 12;
                    innerBox.Height = 200;
                    innerBox.Width = 500;
                    SqlCommand getPallets = new SqlCommand("SELECT quantity, prod_name, cus_name, prod_id, order_num FROM getPallet_Info WHERE route_id = " + globalRoute + " AND pallet_id = " + palletNum, connection5);
                    SqlDataReader grabPallets = getPallets.ExecuteReader();
                    int index = 0;
                    while (grabPallets.Read())
                    {
                        object[] productList = new object[5];
                        grabPallets.GetValues(productList);
                        System.Windows.Controls.ListBoxItem palletItems = new ListBoxItem();
                        palletItems.SetValue(ListBoxItem.HorizontalContentAlignmentProperty, HorizontalAlignment.Center);

                        System.Windows.Controls.StackPanel stkpnl = new StackPanel();
                        stkpnl.SetValue(StackPanel.OrientationProperty, Orientation.Horizontal); stkpnl.SetValue(StackPanel.HorizontalAlignmentProperty, HorizontalAlignment.Left);

                        System.Windows.Controls.TextBlock quantity = new TextBlock();
                        quantity.Width = 100;
                        quantity.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                        quantity.Text = Convert.ToString(productList[0]);

                        System.Windows.Controls.TextBlock prodName = new TextBlock();
                        prodName.Width = 200;
                        prodName.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                        prodName.Text = Convert.ToString(productList[1]);

                        System.Windows.Controls.TextBlock cusName = new TextBlock();
                        cusName.Width = 200;
                        cusName.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                        cusName.Text = Convert.ToString(productList[2]);

                        stkpnl.Children.Add(quantity);
                        stkpnl.Children.Add(prodName);
                        stkpnl.Children.Add(cusName);
                        palletItems.Content = stkpnl;
                        palletItems.Selected += delegate (object sender1, RoutedEventArgs e1) { PalletItems_Selected(sender1, e, palletNum, Convert.ToInt32(productList[3]), Convert.ToInt32(productList[4]), Convert.ToInt32(quantity.Text), prodName.Text, cusName.Text); };
                        innerBox.Items.Add(palletItems);
                        index++;
                    }
                    innerBoxHolder.Content = innerBox;
                    PalletList.Items.Add(innerBoxHolder);
                    connection5.Close();
                }
                connection4.Close();
            }
        }

        private void CreatePallet_Click(object sender, RoutedEventArgs e)
        {
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            
            using (connection)
            {
                connection.Open();
                SqlCommand count = new SqlCommand("SELECT COUNT(DISTINCT palet_id FROM pallet WHERE route_id = " + globalRoute + ";", connection);
                int countPallets = Convert.ToInt32(count.ExecuteScalar());
                int newPalletNum = countPallets + 1;
                int mod = SelectedOrderNum.Count();
                for (int i = 0; i < mod; i++)
                {
                    SqlCommand editpalletID = new SqlCommand("UPDATE pallet SET pallet_id = " + newPalletNum + " WHERE pallet_id = " + SelectedPalletNum[i] + " AND prod_id = " + SelectedProdId[i] + " AND order_num = " + SelectedOrderNum[i] + ";", connection);
                }
                //System.Windows.Controls.ListBoxItem container = new ListBoxItem();
                //container.Content = innerBox;
                //PalletList.Items.Add(container);
                SelectedOrderNum.Clear();
                SelectedPalletNum.Clear();
                SelectedProdId.Clear();
                SelectedcusName.Clear();
                SelectedprodName.Clear();
                SelectedQuantity.Clear();
                connection.Close();
            }
            PalletList.Items.Clear();
            SqlConnection connection4 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            SqlConnection connection5 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection4)
            {
                connection4.Open();
                SqlCommand getCount = new SqlCommand("SELECT COUNT(DISTINCT pallet_id) FROM getPallet_Info WHERE route_id = " + globalRoute, connection4);
                int countNew = Convert.ToInt32(getCount.ExecuteScalar());

                for (int i = 0; i < countNew; i++)
                {
                    int palletNum = i + 1;
                    connection5.Open();
                    System.Windows.Controls.TextBlock palletNumTitle = new TextBlock();
                    System.Windows.Controls.ListBoxItem title1 = new ListBoxItem();
                    palletNumTitle.Text = "Pallet " + palletNum.ToString();
                    palletNumTitle.FontSize = 30;
                    palletNumTitle.SetValue(TextBlock.HorizontalAlignmentProperty, HorizontalAlignment.Center);
                    title1.Content = palletNumTitle;
                    PalletList.Items.Add(title1);
                    System.Windows.Controls.ListBoxItem innerBoxHolder = new ListBoxItem();
                    System.Windows.Controls.ListBox innerBox = new ListBox();
                    innerBox.Name = "pallet" + i.ToString();
                    innerBox.FontSize = 12;
                    innerBox.Height = 200;
                    innerBox.Width = 500;
                    SqlCommand getPallets = new SqlCommand("SELECT quantity, prod_name, cus_name, prod_id, order_num FROM getPallet_Info WHERE route_id = " + globalRoute + " AND pallet_id = " + palletNum, connection5);
                    SqlDataReader grabPallets = getPallets.ExecuteReader();
                    int index = 0;
                    while (grabPallets.Read())
                    {
                        object[] productList = new object[5];
                        grabPallets.GetValues(productList);
                        System.Windows.Controls.ListBoxItem palletItems = new ListBoxItem();
                        palletItems.SetValue(ListBoxItem.HorizontalContentAlignmentProperty, HorizontalAlignment.Center);

                        System.Windows.Controls.StackPanel stkpnl = new StackPanel();
                        stkpnl.SetValue(StackPanel.OrientationProperty, Orientation.Horizontal); stkpnl.SetValue(StackPanel.HorizontalAlignmentProperty, HorizontalAlignment.Left);

                        System.Windows.Controls.TextBlock quantity = new TextBlock();
                        quantity.Width = 100;
                        quantity.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                        quantity.Text = Convert.ToString(productList[0]);

                        System.Windows.Controls.TextBlock prodName = new TextBlock();
                        prodName.Width = 200;
                        prodName.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                        prodName.Text = Convert.ToString(productList[1]);

                        System.Windows.Controls.TextBlock cusName = new TextBlock();
                        cusName.Width = 200;
                        cusName.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
                        cusName.Text = Convert.ToString(productList[2]);

                        stkpnl.Children.Add(quantity);
                        stkpnl.Children.Add(prodName);
                        stkpnl.Children.Add(cusName);
                        palletItems.Content = stkpnl;
                        palletItems.Selected += delegate (object sender1, RoutedEventArgs e1) { PalletItems_Selected(sender1, e, palletNum, Convert.ToInt32(productList[3]), Convert.ToInt32(productList[4]), Convert.ToInt32(quantity.Text), prodName.Text, cusName.Text); };
                        innerBox.Items.Add(palletItems);
                        index++;
                    }
                    innerBoxHolder.Content = innerBox;
                    PalletList.Items.Add(innerBoxHolder);
                    connection5.Close();
                }
                connection4.Close();
            }
        }
            

    }

        /*private void sRoute_SelectionChanged_1(object sender, SelectionChangedEventArgs e)
        {
            palNum = Convert.ToInt32(((sender as ComboBox).SelectedItem as ComboBoxItem).Content); 
        }*/


    class PalletizeOrder : IComparable<PalletizeOrder>
        {
        
            public int orderNum;
            public int stopNum;
            public List<PalletizeProduct> products;

            public PalletizeOrder(int orderNum, int stopNum)
            {
                this.orderNum = orderNum;
                this.stopNum = stopNum;
                products = new List<PalletizeProduct>();
            }

            public int CompareTo(PalletizeOrder that)
            {
                return that.stopNum - this.stopNum;
            }
        }
        class PalletizeProduct
        {
            public int prodID;
            public float volume;
            public int priority;
            public int quantity;
        }
        class PalletizePallet
        {
            // Source: https://pe.usps.com/qsg_archive/PDF/QSG_Archive_20060108/qsg300/q705b.pdf
            public readonly static float MAX_PALLET_VOLUME = 18000;    //fl Oz
            public readonly static int MAX_NUM_PALLETS = PalletizePage.NumPallets;

            public int palletID;
            public float volumeRemaining;
            public List<PalletizeOrder> orders;

            public PalletizePallet(int palletID)
            {
                this.palletID = palletID;
                volumeRemaining = MAX_PALLET_VOLUME;
                orders = new List<PalletizeOrder>();
            }
        }
    
}
