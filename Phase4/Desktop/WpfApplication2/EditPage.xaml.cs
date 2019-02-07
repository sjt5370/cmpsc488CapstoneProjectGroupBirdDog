using System.Windows;
using System.Windows.Controls;

namespace WpfApplication2
{
    public partial class EditPage : Page
    {
        public EditPage()
        {
            InitializeComponent();
        }

        private void returnMainPage(object sender, RoutedEventArgs e)
        {
            
            Edit.Content = new ViewPage();
        }
    }
}
