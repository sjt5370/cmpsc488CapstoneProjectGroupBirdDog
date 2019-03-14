using System;
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
    }
}
