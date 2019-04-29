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



namespace SDesignDesktop {
    /// <summary>
    /// Interaction logic for ViewPage.xaml
    /// </summary>
    public partial class ViewPage : Page {

        public ViewPage() {
            InitializeComponent();
            //updateGUI();
            //UpdateLayout();
        }

        private async void Page_Loaded(object sender, RoutedEventArgs e) {

            Gmaps.Source = new Uri("https://www.google.com/maps/");
            await updateGUI();
            UpdateLayout();
        }

        private void Logout_Click(object sender, RoutedEventArgs e) {
            Gmaps.Dispose();
            SDesignDesktop.Main.GetWindow(this).Content = new LoginPage();
        }

        private static List<int> routesfchange = new List<int>();
        private static List<int> accountfchange = new List<int>();
        private async void AddRoute_Click(object sender, RoutedEventArgs e) {
            await AddRoute_ClickAsync(sender, e);
        }

        private async Task AddRoute_ClickAsync(object sender, RoutedEventArgs e) {
            System.Windows.Controls.ListBoxItem edit = new ListBoxItem();
            System.Windows.Controls.StackPanel stk = new StackPanel();
            stk.SetValue(StackPanel.OrientationProperty, Orientation.Horizontal);
            stk.SetValue(StackPanel.HorizontalAlignmentProperty, HorizontalAlignment.Center);
            edit.SetValue(ListBoxItem.HorizontalContentAlignmentProperty, HorizontalAlignment.Center);

            System.Windows.Controls.TextBlock account = new TextBlock();
            account.Width = 100;
            account.FontSize = 10;
            account.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);

            System.Windows.Controls.TextBlock newRoute = new TextBlock();
            newRoute.Width = 15;
            newRoute.FontSize = 10;
            newRoute.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Right);

            System.Windows.Controls.TextBlock move = new TextBlock();
            move.Width = 35;
            move.FontSize = 10;
            move.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Left);
            move.Text = "Move ";

            System.Windows.Controls.TextBlock toRoute = new TextBlock();
            toRoute.Width = 70;
            toRoute.FontSize = 10;
            toRoute.SetValue(TextBlock.TextAlignmentProperty, TextAlignment.Center);
            toRoute.Text = " To route ";
            newRoute.Text = Convert.ToString(textBox2.Text);

            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection) {
                await connection.OpenAsync();
                SqlCommand cust = new SqlCommand("SELECT cus_name FROM customer_account WHERE acc_id = " + tempID + ";", connection);
                string name = Convert.ToString(await cust.ExecuteScalarAsync());
                account.Text = name;
                connection.Close();
            }

            stk.Children.Add(move);
            stk.Children.Add(account);
            stk.Children.Add(toRoute);
            stk.Children.Add(newRoute);
            edit.Content = stk;
            changes.Items.Add(edit);
            routesfchange.Add(Convert.ToInt32(textBox2.Text));
            accountfchange.Add(tempID);
        }

        private static int tempID = 0;
        private static int tempRoute = 0;
        private static List<Center_Point> centers = new List<Center_Point>();
        private static List<Data> orderData = new List<Data>();
        private static int numCenterPoints = 0;
        private static Stack<Data> orderCoors = new Stack<Data>();
        private static int totalOrders = 0;

        System.Windows.Controls.ListBoxItem route = new ListBoxItem();

        private async void RandomRoutes_Click(object sender, RoutedEventArgs e) {
            await RandomRoutes_ClickAsync(sender, e);
        }
        private async Task RandomRoutes_ClickAsync(object sender, RoutedEventArgs e) {
            routesfchange.Clear();
            accountfchange.Clear();
            totalOrders = 0;
            tempID = 0;
            tempRoute = 0;
            centers.Clear();
            orderData.Clear();
            numCenterPoints = 0;
            orderCoors.Clear();
            int acc;
            int order;
            double Long;
            double Lat;
            string name;
            int total;
            numCenterPoints = Convert.ToInt32(numRoutes.Text);
            double xLow, xHigh, yLow, yHigh;
            double x, y;
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection) {
                await connection.OpenAsync();
                using (SqlCommand orders = new SqlCommand("SELECT * FROM curr_stops", connection)) {
                    using (SqlDataReader reader = await orders.ExecuteReaderAsync()) {
                        if (reader.HasRows) {
                            object[] tot = new object[1];
                            object[] testarr = new object[5];
                            reader.Read();
                            reader.GetValues(testarr);
                            xLow = Convert.ToDouble(testarr[4]);
                            xHigh = Convert.ToDouble(testarr[4]);
                            yLow = Convert.ToDouble(testarr[3]);
                            yHigh = Convert.ToDouble(testarr[3]);
                            while (reader.Read()) {
                                reader.GetValues(testarr);
                                acc = Convert.ToInt32(testarr[0]);
                                order = Convert.ToInt32(testarr[2]);
                                Long = Convert.ToDouble(testarr[4]);
                                Lat = Convert.ToDouble(testarr[3]);
                                name = Convert.ToString(testarr[1]);
                                SqlConnection connection2 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
                                await connection2.OpenAsync();
                                using (SqlCommand ftotal = new SqlCommand("SELECT SUM(quantity) FROM order_item WHERE order_num = " + order + ";", connection2)) {
                                    total = Convert.ToInt32(await ftotal.ExecuteScalarAsync());
                                }
                                connection2.Close();
                                Data temp = new Data(acc, name, order, Lat, Long, total);
                                if (Long < xLow) { xLow = Long; }
                                if (Long > xHigh) { xHigh = Long; }
                                if (Lat < yLow) { yLow = Lat; }
                                if (Lat > yHigh) { yHigh = Lat; }
                                orderCoors.Push(temp);
                                totalOrders++;
                            }
                            connection.Close();
                            for (int i = 0; i < numCenterPoints; i++) {
                                x = random(xHigh, xLow);
                                y = random(yHigh, yLow);
                                centers.Add(new Center_Point(x, y));
                            }
                            ClusterAlg();
                        }
                    }
                }
            }

            connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection) {
                await connection.OpenAsync();
                using (SqlCommand clearPallet = new SqlCommand("DELETE FROM pallet", connection)) {
                    await clearPallet.ExecuteNonQueryAsync();
                }
                using (SqlCommand clear = new SqlCommand("DELETE FROM route_info", connection)) {
                    await clear.ExecuteNonQueryAsync();
                }
                for (int i = 0; i < orderData.Count(); i++) {
                    Data temp = orderData[i];
                    int rt = temp.whichCluster() + 101;
                    using (SqlCommand fill = new SqlCommand("INSERT INTO route_info VALUES (" + rt + ", " + temp.getOrdernum() + ", " + 1 + ")", connection)) {
                        await fill.ExecuteNonQueryAsync();
                    }
                }
                connection.Close();
            }
            await updateGUI();
        }

        public static ListBoxItem tempItem = new ListBoxItem();
        // Method for specifying the stop to view
        public async void order1_MouseDoubleClick(object se, RoutedEventArgs es, int accnum) {
            await order1_MouseDoubleClickAsync(se, es, accnum);
        }
        public async Task order1_MouseDoubleClickAsync(object se, RoutedEventArgs es, int accnum) {
            tempID = accnum;

            textBox.Text = Convert.ToString(accnum);
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
           
            using (connection) {
                await connection.OpenAsync();
                using (SqlCommand getRoute = new SqlCommand("SELECT route_id FROM routelist WHERE acc_id = " + accnum + ";", connection)) {
                    tempRoute = Convert.ToInt32(await getRoute.ExecuteScalarAsync());
                }
                using (SqlCommand getProducts = new SqlCommand("SELECT prod_name, quantity FROM orders WHERE acc_id = " + accnum + ";", connection)) {
                    using (SqlDataReader reader = await getProducts.ExecuteReaderAsync()) {
                        if (listBox.Items.Count != 0) {
                            listBox.Items.Clear();
                        }
                        while (reader.Read()) {
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
                    }
                }
                connection.Close();
            }
        }


        private static void ClusterAlg() {
            double min = 1000;
            double dist = 0;
            int orderCount = 0;
            int ClustNum = 0;
            Boolean finished = false;
            Data nextOrder;

            while (orderCount < totalOrders)
            {
                nextOrder = orderCoors.Pop();
                orderData.Add(nextOrder);
                min = 1000;
                for (int i = 0; i < numCenterPoints; i++)
                {
                    dist = calcDist(nextOrder, centers[i]);
                    if (dist < min)
                    {
                        min = dist;
                        ClustNum = i;
                    }
                }
                nextOrder.setCluster(ClustNum);

                for (int a = 0; a < numCenterPoints; a++)
                {
                    double sumX = 0;
                    double sumY = 0;
                    int ordersInRoute = 0;
                    for (int b = 0; b < orderData.Count; b++)
                    {
                        if (orderData[b].whichCluster() == a)
                        {
                            sumX = sumX + orderData[b].getLong();
                            sumY = sumY + orderData[b].getLat();
                            ordersInRoute++;
                        }
                    }
                    if (ordersInRoute > 0)
                    {
                        centers[a].setX(sumX / ordersInRoute);
                        centers[a].setY(sumY / ordersInRoute);
                    }
                }
                orderCount++;
            }

            while (!finished)
            {
                for (int a = 0; a < numCenterPoints; a++)
                {
                    double sumX = 0;
                    double sumY = 0;
                    int ordersInRoute = 0;
                    for (int b = 0; b < orderData.Count; b++)
                    {
                        if (orderData[b].whichCluster() == a)
                        {
                            sumX = sumX + orderData[b].getLong();
                            sumY = sumY + orderData[b].getLat();
                            ordersInRoute++;
                        }
                    }
                    if (ordersInRoute > 0)
                    {
                        centers[a].setX(sumX / ordersInRoute);
                        centers[a].setY(sumY / ordersInRoute);
                    }
                }
                finished = true;
                for (int i = 0; i < orderData.Count; i++)
                {
                    Data temp = orderData[i];
                    min = 1000;
                    for (int j = 0; j < centers.Count; j++)
                    {
                        dist = calcDist(temp, centers[j]);
                        if (dist < min)
                        {
                            min = dist;
                            ClustNum = j;
                        }
                    }
                    if (temp.whichCluster() != ClustNum)
                    {
                        temp.setCluster(ClustNum);
                        orderData[i] = temp;
                        finished = false;
                    }
                }
            }
        }
        private static double calcDist(Data d, Center_Point c) {
            double distY = Math.Pow((c.getY() - d.getLat()), 2);
            double distX = Math.Pow((c.getX() - d.getLong()), 2);
            double EuclidDist = Math.Sqrt(distY + distX);
            return EuclidDist;
        }
        public double random(double max, double min) {
            Random random = new Random();
            double randMult = random.NextDouble();
            return min + (randMult * (max - min));
        }
        private class Center_Point {
            private double xCoord = 0;
            private double yCoord = 0;
            public Center_Point() { }
            public Center_Point(double lngX, double latY) {
                this.xCoord = lngX;
                this.yCoord = latY;
            }
            public void setX(double lngX) { this.xCoord = lngX; }
            public void setY(double latY) { this.yCoord = latY; }
            public double getX() { return this.xCoord; }
            public double getY() { return this.yCoord; }
        }

        private class Data {
            private string name = "";
            private int ordernum = 0;
            private int ID = 0;
            private double nX = 0;
            private double nY = 0;
            private int nCluster = 0;
            private int total = 0;

            public Data() { }
            public Data(int ID, string name, int ordernum, double y, double x, int total) {
                this.total = total;
                this.ID = ID;
                this.name = name;
                this.ordernum = ordernum;
                this.nX = x;
                this.nY = y;
            }
            public void setTotal(int tot) { this.total = tot; }
            public int getTotal() { return this.total; }
            public void setID(int id) { this.ID = id; }
            public int getID() { return this.ID; }
            public void setName(string name) { this.name = name; }
            public string getName() { return this.name; }
            public void setOrdernum(int order) { this.ordernum = order; }
            public int getOrdernum() { return this.ordernum; }
            public void setLong(double x) { this.nX = x; }
            public double getLong() { return this.nX; }
            public void setLat(double y) { this.nY = y; }
            public double getLat() { return this.nY; }
            public void setCluster(int clustNum) { this.nCluster = clustNum; }
            public int whichCluster() { return this.nCluster; }
        }

        private void NumUp_Click(object sender, RoutedEventArgs e) {
            numRoutes.Text = Convert.ToString(Convert.ToInt32(numRoutes.Text) + 1);
        }

        private void NumDown_Click(object sender, RoutedEventArgs e) {
            numRoutes.Text = Convert.ToString(Convert.ToInt32(numRoutes.Text) - 1);
        }

        private async void GenOrders_Click(object sender, RoutedEventArgs e) {
            await GenOrders_ClickAsync(sender, e);
        }
        private async Task GenOrders_ClickAsync(object sender, RoutedEventArgs e) {
            //////////////////////////////////////////////////////////////////////////////////////////////
            //                                 Populate Customer Master and Coords                     //
            /////////////////////////////////////////////////////////////////////////////////////////////
            string[] adjective = new string[] { "Salty", "Stinky", "Blue", "Black", "Tiny", "Huge", "Lazy", "Strong", "Drunken", "International", "Super", "American", "Colorful", "Ugly", "Modern", "Old", "Red", "Dirty", "Quick", "Slow" };
            string[] noun = new string[] { "Glass", "Friday", "Mug", "Chicken", "Duck", "Student", "Burger", "Lion", "Tavern", "Paris", "Truck", "Palm", "Corgi", "Town", "Tree", "Web", "Song", "Shirt", "Tower", "Ride" };
            string[] number = new string[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
            string[] letter = new string[] { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z" };//26
            string addr_street = "not null Street";
            string addr_city = "not null city";
            string addr_state = "NA";
            int addr_zip = 10101;
            string num;
            string[] usernames = new string[400];
            string[] passwords = new string[400];
            int[] accountIDs = new int[400];
            string[] name = new string[400];
            string[] emails = new string[400];
            double[] coordsx = new double[400];
            double[] coordsy = new double[400];
            Random rand = new Random();
            for (int i = 0; i < 400; i++) {
                int ly = rand.Next(39, 42);
                int ry = rand.Next(0, 999999);
                int lx = rand.Next(75, 80);
                int rx = rand.Next(0, 999999);
                double y = ly + (ry * 0.000001);
                double x = lx + (rx * 0.000001);
                coordsx[i] = x * -1;
                coordsy[i] = y;
            }
            int count = 0;
            for (int i = 0; i < 20; i++) {
                for (int j = 0; j < 20; j++) {
                    string acc_name = "The " + adjective[i] + " " + noun[j];
                    string username = adjective[i] + noun[j] + number[rand.Next(8)] + number[rand.Next(8)];
                    string password = noun[i] + adjective[j] + "@" + number[rand.Next(8)] + number[rand.Next(8)];
                    if (count < 10) {
                        num = number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString();
                    } else if (count < 100 && count > 9) {
                        num = number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString();
                    } else {
                        num = number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString();
                    }
                    string account = num + count.ToString();
                    int accountID = Convert.ToInt32(account);
                    emails[count] = adjective[i] + noun[j] + "@gmail.com";
                    accountIDs[count] = accountID;
                    usernames[count] = username;
                    passwords[count] = password;
                    name[count] = acc_name;
                    count++;
                }
            }

            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection) {
                await connection.OpenAsync();
                for (int i = 0; i < 400; i++) {
                    using (SqlCommand updateMaster = new SqlCommand("Insert into master_account values (" + accountIDs[i] + ", " + 1 + ", '" + usernames[i] + "', '" + passwords[i] + "');", connection)) {
                        await updateMaster.ExecuteNonQueryAsync();
                    }
                    using (SqlCommand updateCustomer = new SqlCommand("Insert into customer_account values (" + accountIDs[i] + ", '" + emails[i] + "', '" + name[i] + "', '" + addr_street + "', '" + addr_city + "', '" + addr_state + "', " + addr_zip + ");", connection)) {
                        await updateCustomer.ExecuteNonQueryAsync();
                    }
                    using (SqlCommand updateCoords = new SqlCommand("Insert into coordinates values (" + accountIDs[i] + ", " + coordsy[i] + ", " + coordsx[i] + ");", connection)) {
                        await updateCoords.ExecuteNonQueryAsync();
                    }
                }
                connection.Close();
            }

            //////////////////////////////////////////////////////////////////////////////////////////////
            //                                 Populate Inventory and Product Table                     //
            /////////////////////////////////////////////////////////////////////////////////////////////
            Random rand1 = new Random();
            string[] beer = new string[] {
                "2SP Beer","Ballast Point Beer","Bartles & Jaymes Beer","Boones Farm Beer","Brewdog Beer","Camo Beer","Dogfish Head Beer","Firestone Walker Beer","Flying Fish Beer","Great Lakes Beer","Guinness Beer","Harpoon Beer","Heavy Seas Beer","Jack Cider Beer","Lagunitas Beer","Long Trail Beer","Peak Beer","Saranac Beer","Shiner Beer","Smirnoff Ice Beer","Spring House Beer","Stoudts Beer","Strongbow Beer","Sam Adams Beer","Angry Orchard Beer","Twisted Tea Beer","Weyerbacher Beer","Woodchuck Cider","Corona","Miller Lite","Coors Light","Old Millwakee","Labatt Blue","Reds Apple Ale"
            };//34 Brands
            string[] size = new string[] { "12 oz NR 4/6", "12oz NR LS", "40oz NR(12)", "12 oz NR 2/12", "12 oz CAN 4/6", "12oz CAN LS", "12 oz CAN 2/12", "24 oz NR (12)", "24 oz CAN (12)", "16 oz CAN 6/4", "1/2 Keg", "1/4 Keg", "1/6 Keg" };//13 Sizes
            //string[] number = new string[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" };
            int[] coresponding_volumes = new int[] { 280, 280, 400, 280, 160, 160, 160, 280, 130, 240, 1500, 900, 600 };
            string[] product_names = new string[442];
            int[] prod_IDs = new int[442];
            int[] volumes = new int[442];
            int[] priority = new int[] { 1, 1, 2, 1, 3, 3, 3, 2, 4, 5, 0, 0, 0 };
            int[] priorities = new int[442];
            double[] prices = new double[442];
            int[] shelf = new int[442];
            int[] bulk = new int[442];
            int[] flag = new int[442];
            int counter = 0;
            string[] beers = new string[442];
            string[] desc = new string[442];
            //string num;
            for (int i = 0; i < 34; i++) {
                string brand = beer[i];
                for (int j = 0; j < 13; j++) {
                    beers[counter] = beer[i];
                    desc[counter] = desc[j];
                    string prod = brand + " " + size[j];
                    product_names[counter] = prod;
                    priorities[counter] = priority[j];
                    volumes[counter] = coresponding_volumes[j];
                    bulk[counter] = rand1.Next(2000, 10000);
                    shelf[counter] = rand1.Next(0, 80);
                    flag[counter] = 1;
                    if (counter < 10) {
                        num = number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString();
                    } else if (counter < 100 && counter > 9) {
                        num = number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString();
                    } else {
                        num = number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString();
                    }
                    string p_id = counter.ToString() + num;
                    prod_IDs[counter] = Convert.ToInt32(p_id);

                    if (j < 10) {
                        prices[counter] = rand1.Next(18, 60) + 0.99;
                    } else if (j == 10) {
                        prices[counter] = rand1.Next(90, 280) + 0.99;
                    } else if (j == 11) {
                        prices[counter] = rand1.Next(60, 120) + 0.99;
                    } else {
                        prices[counter] = rand1.Next(60, 150) + 0.99;
                    }
                    counter++;
                }
            }

            connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection) {
                await connection.OpenAsync();
                for (int i = 0; i < 442; i++) {
                    using (SqlCommand updateProduct = new SqlCommand("Insert into product values (" + prod_IDs[i] + ", '" + product_names[i] + "', '" + beers[i] + "', '" + desc[i] + "', " + prices[i] + ", " + priorities[i] + ", " + volumes[i] + ");", connection)) {
                        await updateProduct.ExecuteNonQueryAsync();
                    }
                    using (SqlCommand updateInventory = new SqlCommand("Insert into inventory values (" + prod_IDs[i] + ", " + bulk[i] + ", " + shelf[i] + ", " + flag[i] + ");", connection)) {
                        await updateInventory.ExecuteNonQueryAsync();
                    }
                }
                connection.Close();
            }

            //////////////////////////////////////////////////////////////////////////////////////////////
            //                                 Populate order_full and order_item                      //
            /////////////////////////////////////////////////////////////////////////////////////////////
            int ordcount2 = 0;
            int complete = 0;
            int active = 1;
            int urgency = 10;
            int[] ordernums = new int[300];

            connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection) {
                await connection.OpenAsync();
                for (int i = 0; i < 300; i++) {
                    int ID = accountIDs[i];
                    ordernums[i] = ordcount2;
                    int sku = rand.Next(1, 10);
                    using (SqlCommand updateOrderfull = new SqlCommand("Insert into order_full values (" + ordcount2 + ", " + ID + ", " + complete + ", " + urgency + ", " + active + ");", connection)) {
                        await updateOrderfull.ExecuteNonQueryAsync();
                    }
                    bool[] check = new bool[442];
                    for (int j = 0; j < sku; j++) {
                        int prod_id_index = rand.Next(442);
                        while (check[prod_id_index] == true) {
                            prod_id_index = rand.Next(442);
                        }
                        check[prod_id_index] = true;
                        int quantity;
                        if (volumes[prod_id_index] < 600) {
                            quantity = rand.Next(1, 15);
                        } else {
                            quantity = rand.Next(1, 6);
                        }

                        using (SqlCommand updateOrderitem = new SqlCommand("Insert into order_item values (" + ordcount2 + ", " + prod_IDs[prod_id_index] + ", " + quantity + ");", connection)) {
                            await updateOrderitem.ExecuteNonQueryAsync();
                        }
                    }
                    ordcount2++;
                }
                connection.Close();
            }
        }

        private void View_Navigated(object sender, NavigationEventArgs e) { }

        private async void toChange_Click(object sender, RoutedEventArgs e) {
            await toChange_ClickAsync(sender, e);
        }
        private async Task toChange_ClickAsync(object sender, RoutedEventArgs e) {
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection) {
                await connection.OpenAsync();
                int count = routesfchange.Count();
                progress.Minimum = 0;
                progress.Maximum = count;
                progress.Value = 1;
                for (int i = 0; i < count; i++) {
                    SqlCommand change = new SqlCommand("UPDATE route_info SET route_id = " + routesfchange[i] + "FROM order_full WHERE order_full.order_num = route_info.order_num AND order_full.acc_id = " + accountfchange[i] + ";", connection);
                    await change.ExecuteNonQueryAsync();
                    updateProgress();
                }
                connection.Close();
            }
            await updateGUI();
            changes.Items.Clear();
        }

        private void updateProgress() {
            progress.Value = progress.Value + 1;
            //progress.UpdateLayout();
        }

        private void toDelete_Click(object sender, RoutedEventArgs e) {
            int x = changes.SelectedIndex;
            routesfchange.RemoveAt(x);
            accountfchange.RemoveAt(x);
            changes.Items.RemoveAt(x);
        }

        private int checkCount;
        public async Task updateGUI() {
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection) {
                await connection.OpenAsync();
                SqlCommand getNumberOfRoutes = new SqlCommand("SELECT COUNT(DISTINCT route_id) FROM route_info", connection);
                SqlDataReader numRts = await getNumberOfRoutes.ExecuteReaderAsync();
                numRts.Read();
                checkCount = numRts.GetInt32(0);
                numRts.Close();
                connection.Close();
            }

            int count;
            if (checkCount == 0) {
                count = numCenterPoints + 1;
            } else {
                count = checkCount + 1;
            }

            RouteList.Items.Clear();
            for (int i = 1; i < count; i++) {
                int routenum = 100 + i;
                System.Windows.Controls.ListBoxItem routeTitle = new ListBoxItem();
                routeTitle.Content = "Route " + routenum;
                routeTitle.FontSize = 20;
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

                connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
                using (connection) {
                    await connection.OpenAsync();
                    SqlCommand getRoutes = new SqlCommand("SELECT * FROM routelist WHERE route_id = " + routenum + ";", connection);
                    using (SqlDataReader readRoutes = await getRoutes.ExecuteReaderAsync()) {
                        while (readRoutes.Read()) {
                            System.Windows.Controls.ListBoxItem edit = new ListBoxItem();
                            edit.SetValue(ListBoxItem.HorizontalContentAlignmentProperty, HorizontalAlignment.Center);

                            System.Windows.Controls.StackPanel stk = new StackPanel();
                            stk.SetValue(StackPanel.OrientationProperty, Orientation.Horizontal);
                            stk.SetValue(StackPanel.HorizontalAlignmentProperty, HorizontalAlignment.Left);

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
                            await connection2.OpenAsync();
                            SqlCommand getTotals = new SqlCommand("SELECT SUM(quantity) FROM orders WHERE acc_id    = " + readRoutes.GetInt32(0) + ";", connection2);
                            string tot = Convert.ToString(await getTotals.ExecuteScalarAsync());
                            connection2.Close();
                            totalbox.Text = tot;

                            stk.Children.Add(account);
                            stk.Children.Add(name);
                            stk.Children.Add(totalbox);
                            edit.Content = stk;
                            edit.Selected += async delegate (object se, RoutedEventArgs es) { await order1_MouseDoubleClickAsync(se, es, Convert.ToInt32(account.Text)); };
                            orders.Items.Add(edit);
                        }
                        route.Content = orders;
                        RouteList.Items.Add(route);
                        readRoutes.Close();
                    }
                    connection.Close();
                }
            }
        }
        
        private void toPalletizePage_Click(object sender, RoutedEventArgs e) {
            Gmaps.Dispose();
            SDesignDesktop.Main.GetWindow(this).Content = new PalletizePage();
        }
    }
}

