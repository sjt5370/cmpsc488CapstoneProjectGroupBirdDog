using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;

namespace WpfApplication2
{
    public partial class ViewPage : Page
    {
        public ViewPage()
        {
            InitializeComponent();
        }

        private void ButtonBase_OnClick(object sender, RoutedEventArgs e)
        {
            View.Content = new EditAdd();
        }

        private void Control_OnMouseDoubleClick(object sender, MouseButtonEventArgs e)
        {
            View.Content = new InspectPage();
        }
    }
}
