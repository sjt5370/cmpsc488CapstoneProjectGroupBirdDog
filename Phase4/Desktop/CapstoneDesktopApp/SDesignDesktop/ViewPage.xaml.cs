using System;
using System.Collections.Generic;
using System.Data.SqlClient;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
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
    /// Interaction logic for ViewPage.xaml
    /// </summary>
    public partial class ViewPage : Page
    {

        public ViewPage()
        {
            InitializeComponent();
        }

        private void Page_Loaded(object sender, RoutedEventArgs e)
        {

            Gmaps.Source = new Uri("https://www.google.com/maps/");
        }

        private void Logout_Click(object sender, RoutedEventArgs e)
        {
            Gmaps.Dispose();
            SDesignDesktop.Main.GetWindow(this).Content = new LoginPage();
        }

        private void AddRoute_Click(object sender, RoutedEventArgs e)
        {
            Window add = new AddRoute();
            add.Show();
        }
        private static List<Center_Point> centers = new List<Center_Point>();
        private static List<Data> orderData = new List<Data>();
        private static int numCenterPoints = 0;
        private void RandomRoutes_Click(object sender, RoutedEventArgs e)
        {

            numCenterPoints = Convert.ToInt32(numRoutes.Text);
            double xLow, xHigh, yLow, yHigh;
            //query database to find the above double values for the coordinates in the orders for today
            double x, y;
            /*for(int i = 0; i < numCenterPoints; i++)
            {
                x = random(xHigh, xLow);
                y = random(yHigh, yLow);
                centers.Add(new Center_Point(x, y));
            }*/
            ClusterAlg();
        }

        private static void ClusterAlg()
        {
            int totalOrders = 0;
            double min = 1000;
            double dist = 0;
            int orderCount = 0;
            int ClustNum = 0;
            Boolean finished = false;
            Data nextOrder = null;
            Stack<Data> orderCoors = new Stack<Data>();
            //Grab every set of coordinates insert into orderCoors as Data type objects
            //increase the value of total orders with each push

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
                            sumX = sumX + orderData[b].getX();
                            sumY = sumY + orderData[b].getY();
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
                            sumX = sumX + orderData[b].getX();
                            sumY = sumY + orderData[b].getY();
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
                    for (int j = 0; j < orderData.Count; j++)
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
        private static double calcDist(Data d, Center_Point c)
        {
            double distY = Math.Pow((c.getY() - d.getY()), 2);
            double distX = Math.Pow((c.getX() - d.getX()), 2);
            double EuclidDist = Math.Sqrt(distY + distX);
            return EuclidDist;
        }
        public double random(double max, double min)
        {
            Random random = new Random();
            double randMult = random.NextDouble();
            return min + (randMult * (max - min));
        }
        private class Center_Point
        {
            private double xCoord = 0;
            private double yCoord = 0;
            public Center_Point()
            {
                return;
            }
            public Center_Point(double lngX, double latY)
            {
                this.xCoord = lngX;
                this.yCoord = latY;
                return;
            }
            public void setX(double lngX)
            {
                this.xCoord = lngX;
                return;
            }
            public void setY(double latY)
            {
                this.yCoord = latY;
                return;
            }
            public double getX()
            {
                return this.xCoord;
            }
            public double getY()
            {
                return this.yCoord;
            }

        }

        private class Data
        {
            private double nX = 0;
            private double nY = 0;
            private int nCluster = 0;

            public Data()
            {
                return;
            }
            public Data(double x, double y)
            {
                this.nX = x;
                this.nY = y;
                return;
            }
            public void setX(double x)
            {
                this.nX = x;
                return;
            }
            public double getX()
            {
                return this.nX;
            }
            public void setY(double y)
            {
                this.nY = y;
                return;
            }
            public double getY()
            {
                return this.nY;
            }
            public void setCluster(int clustNum)
            {
                this.nCluster = clustNum;
                return;
            }
            public int whichCluster()
            {
                return this.nCluster;
            }
        }

        private void NumUp_Click(object sender, RoutedEventArgs e)
        {
            numRoutes.Text = Convert.ToString(Convert.ToInt32(numRoutes.Text) + 1);
        }

        private void NumDown_Click(object sender, RoutedEventArgs e)
        {
            numRoutes.Text = Convert.ToString(Convert.ToInt32(numRoutes.Text) - 1);
        }

        private void GenOrders_Click(object sender, RoutedEventArgs e)
        {
            //////////////////////////////////////////////////////////////////////////////////////////////
            //                                 Populate Customer Master and Coords                     //
            /////////////////////////////////////////////////////////////////////////////////////////////
            /*string[] adjective = new string[] { "Salty", "Stinky", "Blue", "Black", "Tiny", "Huge", "Lazy", "Strong", "Drunken", "International", "Super", "American", "Colorful", "Ugly", "Modern", "Old", "Red", "Dirty", "Quick", "Slow" };
            string[] noun = new string[] { "Glass", "Friday", "Mug", "Chicken", "Duck", "Student", "Burger", "Lion", "Tavern", "Paris", "Truck", "Palm", "Corgi", "Town", "Tree", "Web", "Song", "Shirt", "Tower", "Ride" };
            string[] number = new string[] {"1","2","3","4","5","6","7","8","9"};
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
            for (int i = 0; i < 400; i++)
            {
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
            for (int i = 0; i < 20; i++)
            {
                for (int j = 0; j < 20; j++)
                {
                    string acc_name = "The " + adjective[i] + " " + noun[j];
                    string username = adjective[i] + noun[j] + number[rand.Next(8)] + number[rand.Next(8)];
                    string password = noun[i] + adjective[j] + "@" + number[rand.Next(8)] + number[rand.Next(8)];
                    if (count < 10)
                    {
                        num = number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString();
                    }
                    else if (count < 100 && count > 9)
                    {
                        num = number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString() + number[rand.Next(8)].ToString();
                    }
                    else
                    {
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
            using (connection)
            {
                connection.Open();

                for (int i = 0; i < 400; i++)
                {
                    using (SqlCommand updateMaster = new SqlCommand("Insert into master_account values (" + accountIDs[i] + ", " + 1 +
                        ", '" + usernames[i] + "', '" + passwords[i] + "');", connection))
                    {
                        updateMaster.ExecuteNonQuery();
                    }
                    using (SqlCommand updateCustomer = new SqlCommand("Insert into customer_account values (" + accountIDs[i] + ", '" + emails[i] + "', '" + name[i] + "', '" + addr_street + "', '" + addr_city + "', '" + addr_state + "', " + addr_zip + ");", connection))
                    {
                        updateCustomer.ExecuteNonQuery();
                    }
                    using (SqlCommand updateCoords = new SqlCommand("Insert into coordinates values (" + accountIDs[i] + ", " + coordsy[i] + ", " + coordsx[i] + ");", connection))
                    {
                        updateCoords.ExecuteNonQuery();
                    }
                }
                connection.Close();
            }

            //////////////////////////////////////////////////////////////////////////////////////////////
            //                                 Populate Inventory and Product Table                     //
            /////////////////////////////////////////////////////////////////////////////////////////////
            Random rand1 = new Random();
            string[] beer = new string[]
            {
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
            //string num;
            for (int i = 0; i < 34; i++)
            {
                string brand = beer[i];
                for (int j = 0; j < 13; j++)
                {
                    string prod = brand + " " + size[j];
                    product_names[counter] = prod;
                    priorities[counter] = priority[j];
                    volumes[counter] = coresponding_volumes[j];
                    bulk[counter] = rand1.Next(2000, 10000);
                    shelf[counter] = rand1.Next(0, 80);
                    flag[counter] = 1;
                    if (counter < 10)
                    {
                        num = number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString();
                    }
                    else if (counter < 100 && counter > 9)
                    {
                        num = number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString();
                    }
                    else
                    {
                        num = number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString() + number[rand1.Next(8)].ToString();
                    }
                    string p_id = counter.ToString() + num;
                    prod_IDs[counter] = Convert.ToInt32(p_id);

                    if (j < 10)
                    {
                        prices[counter] = rand1.Next(18, 60) + 0.99;
                    }
                    else if (j == 10)
                    {
                        prices[counter] = rand1.Next(90, 280) + 0.99;
                    }
                    else if (j == 11)
                    {
                        prices[counter] = rand1.Next(60, 120) + 0.99;
                    }
                    else
                    {
                        prices[counter] = rand1.Next(60, 150) + 0.99;
                    }
                    counter++;
                }
            }

            SqlConnection connection1 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection1)
            {
                connection1.Open();
                for (int i = 0; i < 442; i++)
                {
                    using (SqlCommand updateProduct = new SqlCommand("Insert into product values (" + prod_IDs[i] + ", '" + product_names[i] + "', '" + "" + "', '" + "" + "', " + prices[i] + ", " + priorities[i] + ", " + volumes[i] + ");", connection1))
                    {
                        updateProduct.ExecuteNonQuery();
                    }
                    using (SqlCommand updateInventory = new SqlCommand("Insert into inventory values (" + prod_IDs[i] + ", " + bulk[i] + ", " + shelf[i] + ", " + flag[i] + ");", connection1))
                    {
                        updateInventory.ExecuteNonQuery();
                    }
                }
                connection1.Close();
            }
            
            //////////////////////////////////////////////////////////////////////////////////////////////
            //                                 Populate order_full and order_item                      //
            /////////////////////////////////////////////////////////////////////////////////////////////
            int ordcount2 = 0;
            int complete = 0;
            int active = 1;
            int urgency = 10;
            int[] ordernums = new int[300];
            SqlConnection connection2 = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection2)
            {
                connection2.Open();

                for (int i = 0; i < 300; i++)
                {
                    int ID = accountIDs[i];
                    ordernums[i] = ordcount2;
                    int sku = rand.Next(35);
                    using (SqlCommand updateOrderfull = new SqlCommand("Insert into order_full values (" + ordernums[i] + ", " + ID + ", " + complete + ", " + urgency + ", " + active + ");", connection2))
                    {
                        updateOrderfull.ExecuteNonQuery();
                    }
                    bool[] check = new bool[442];
                    for (int j = 0; j < sku; j++)
                    {
                        int prod_id_index = rand.Next(442);
                        while (check[prod_id_index] == true)
                        {
                            prod_id_index = rand.Next(442);
                        }
                        check[prod_id_index] = true;
                        int quantity;
                        if(volumes[prod_id_index] < 600)
                        {
                            quantity = rand.Next(1,60);
                        }
                        else
                        {
                            quantity = rand.Next(1, 6);
                        }

                        using (SqlCommand updateOrderitem = new SqlCommand("Insert into order_item values (" + ordernums[i] + ", " + prod_IDs[prod_id_index] + ", " + quantity + ");", connection2))
                        {
                            updateOrderitem.ExecuteNonQuery();
                        }
                    }
                    ordcount2++;
                }
                connection2.Close();
            }*/
        }


    }
}

