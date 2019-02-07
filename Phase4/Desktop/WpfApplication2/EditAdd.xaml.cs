using System.Windows;
using System.Windows.Controls;

namespace WpfApplication2
{
    public partial class EditAdd : Page
    {
        public EditAdd()
        {
            InitializeComponent();
            
        }

        private void ClickEdit(object sender, RoutedEventArgs e)
        {
            Selection.Content = new EditPage();
        }

        private void ClickAdd(object sender, RoutedEventArgs e)
        {
            Selection.Content = new AddPage();
        }
    }
}
