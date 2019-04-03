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

namespace dbConnector
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();
        }

        private void ButtonInsertIntoTable_Click(object sender, RoutedEventArgs e)
        {
            String tableName = textBoxTableName.Text;
            this.Content = new InsertPage(tableName);            
        }

        private void ButtonGetFromTable_Click(object sender, RoutedEventArgs e)
        {
            String tableName = textBoxTableName.Text;
            GetPage GP = new GetPage(tableName);
            this.Content = GP.Content;
            
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            List data = new List();
            using (SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;"))
            using (SqlCommand cmd = new SqlCommand("SELECT * FROM product WHERE prod_id = 1", connection))
            {
                connection.Open();
                using (SqlDataReader reader = cmd.ExecuteReader())
                {
                    // Check is the reader has any rows at all before starting to read.
                    if (reader.HasRows)
                    {
                        // Read advances to the next row.
                        while (reader.Read())
                        {
                            this.textBoxData.Text = this.textBoxData.Text + reader.GetString(reader.GetOrdinal("prod_name"))+ " " + reader.GetString(reader.GetOrdinal("prod_desc"));
                        }
                    }
                }
            }
        }
    }
}
