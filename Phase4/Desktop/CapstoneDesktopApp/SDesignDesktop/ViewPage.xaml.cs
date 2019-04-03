using System;
using System.Collections;
using System.Collections.Generic;
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
        private static ArrayList centers = new ArrayList();
        private static ArrayList orderData = new ArrayList();
        private void RandomRoutes_Click(object sender, RoutedEventArgs e)
        {
            int numCenterPoints = Convert.ToInt32(numRoutes.Text);
            double xLow, xHigh, yLow, yHigh;
            //query database to find the above double values for the coordinates in the orders for today
            double x, y;
            for(int i = 0; i < numCenterPoints; i++)
            {
                x = random(xHigh, xLow);
                y = random(yHigh, yLow);
                centers.Add(new Center_Point(x, y));
            }
            ClusterAlg();
        }

        private static void ClusterAlg()
        {
            int totalOrders; //Number of orders for the day
            double min = 1000;
            double dist = 0;
            int orderCount = 0;
            int ClustNum = 0;
            Boolean finished = false;
            Data nextOrder = null;

            while (orderCount < totalOrders)
            {

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
            public Data(double x,double y)
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
    }
}
