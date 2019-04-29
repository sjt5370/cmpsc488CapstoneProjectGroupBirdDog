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
using System.Data.SqlClient;

namespace SDesignDesktop {
    /// <summary>
    /// Interaction logic for LoginPage.xaml
    /// </summary>
    public partial class LoginPage : Page {
        public LoginPage() {
            InitializeComponent();
        }

        private async void Loginbutton_Click(object sender, RoutedEventArgs e) {
            String username = UsernameTextBox.Text;
            String password = PasswordTextBox.Password.ToString();
            await Temp(username, password);
        }

        private async Task Temp(String username, String password) {
            if (await Loginbutton_ClickAsync(username, password))
                SDesignDesktop.Main.GetWindow(this).Content = new ViewPage();
        }

        private async Task<bool> Loginbutton_ClickAsync (String username, String password) {
            bool retVal = false;
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection) {
                await connection.OpenAsync();
                SqlCommand getId = new SqlCommand("SELECT * FROM master_account INNER JOIN employee_account ON master_account.acc_id = employee_account.acc_id WHERE username = '" + username + "' AND password = '" + password + "' AND job = 'Supervisor';", connection);
                SqlDataReader reader = await getId.ExecuteReaderAsync();
                retVal = reader.HasRows;
                reader.Close();
                connection.Close();
            }
            return retVal;
            //login.Content = new ViewPage();
            //SDesignDesktop.Main.GetWindow(this).Content = new ViewPage();
        }

        private void Hyperlink_Click(object sender, RoutedEventArgs e) {
            SDesignDesktop.Main.GetWindow(this).Content = new createAcc();
        }
    }
}