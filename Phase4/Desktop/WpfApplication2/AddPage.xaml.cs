using System.Windows;
using System.Windows.Controls;

namespace WpfApplication2
{
    public partial class AddPage : Page
    {
        public AddPage()
        {
            InitializeComponent();
        }

        private void returnMainPage(object sender, RoutedEventArgs e)
        {
           
            Add.Content = new ViewPage();
        }
    }
}
