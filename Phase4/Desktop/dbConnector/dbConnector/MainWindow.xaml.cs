using System;
using System.Collections.Generic;
using System.Data;
using System.Data.SqlClient;
using System.IO;
using System.Linq;
using System.Net;
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
            int id = 0;
            string address = "1600+Pennsylvania+Ave+NW,Washington,DC,20500";
            /*using (SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;"))
            using (SqlCommand cmd = new SqlCommand("SELECT * FROM customer_account WHERE acc_id = " + id, connection))
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
                            address = reader.GetString(reader.GetOrdinal("addr_street")) + " " + reader.GetString(reader.GetOrdinal("addr_city")) + " " + reader.GetString(reader.GetOrdinal("addr_state")) + " " + reader.GetString(reader.GetOrdinal("addr_zip"));
                        }
                    }
                }
                connection.Close();
            } */

            
            string url = "http://www.mapquestapi.com/geocoding/v1/address?key=nOXiWChDoFPzIEHhm2YCilplmnTr0WBh&location=" + address;
            WebRequest request = WebRequest.Create(url);
            using (WebResponse response = (HttpWebResponse)request.GetResponse())
            {
                using (StreamReader reader = new StreamReader(response.GetResponseStream(), Encoding.UTF8))
                {
                    this.textBoxData.Text = this.textBoxData.Text + reader.ReadToEnd().ToString();                                      
                }
            }
        }
    }
}
