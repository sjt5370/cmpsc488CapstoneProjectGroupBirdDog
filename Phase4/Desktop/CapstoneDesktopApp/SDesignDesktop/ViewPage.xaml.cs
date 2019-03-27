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

        }

        public double random(double max, double min)
        {
            Random random = new Random();
            double randMult = random.NextDouble();
            return min + (randMult * (max - min));
        }
        private class Center_Point
        {
            private double xCoord = 0.0;
            private double yCoord = 0.0;
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
