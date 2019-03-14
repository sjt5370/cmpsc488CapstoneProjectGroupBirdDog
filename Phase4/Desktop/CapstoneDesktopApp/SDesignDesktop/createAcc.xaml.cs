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

namespace SDesignDesktop
{
    /// <summary>
    /// Interaction logic for createAcc.xaml
    /// </summary>
    public partial class createAcc : Page
    {
        public createAcc()
        {
            InitializeComponent();
        }

        private void CreatAccount_Click(object sender, RoutedEventArgs e)
        {
            String accType = typeSelect.SelectedValue.ToString();
            String name = cusEmpName.Text;
            String username = usernameIn.Text;
            String password = passwordIn.Text;
            String addStreet = addressIn.Text;
            String addCity = cityIn.Text;
            String addZip = zipcodeIn.Text;
            String addState = stateIn.Text;
            String accIdInt = AccIdGenerator();
            String job = EmployeeJobGen();

            if(accType == "Customer Account")
            {
                CreateCustomerAccount(name, accIdInt, 1, username, password, addStreet, addCity, addState, addZip);
            } else if(accType == "Employee Account")
            {
                CreateEmployeeAccount(name, accIdInt, 0, username, password, job);
            }
            SDesignDesktop.Main.GetWindow(this).Content = new LoginPage();
        }

        public void CreateEmployeeAccount(String name, String accId, int accType, String username, String password, String jobType)
        {
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection)
            {
                connection.Open();
                using (SqlCommand updateMaster = new SqlCommand("Insert into master_account values (" + accId + ", " + accType +
                        ", '" + username + "', '" + password + "')", connection))
                {
                    updateMaster.ExecuteNonQuery();
                }
                using (SqlCommand updateEmployee = new SqlCommand("Insert into employee_account values (" + accId +
                            ", '" + name + "', 'Doe', '"+ jobType +"', " + 0 + ")", connection))
                {
                    updateEmployee.ExecuteNonQuery();
                }
                connection.Close();
            }
        }

        public void CreateCustomerAccount(String name, String accId, int accType, String username, String password, String Street, String City, String State, String zipcode)
        {
            SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;");
            using (connection)
            {
                connection.Open();
                using (SqlCommand updateMaster = new SqlCommand("Insert into master_account values (" + accId + ", " + accType +
                        ", '" + username + "', '" + password + "')", connection))
                {
                    updateMaster.ExecuteNonQuery();
                }
                using (SqlCommand updateCustomer = new SqlCommand("Insert into customer_account values (" + accId + ",'email', '" + name + "', '" + Street + "', '" + City + "', '" + State + "', '" + zipcode + "')", connection))
                {
                    updateCustomer.ExecuteNonQuery();
                }
                connection.Close();
            }
        }

        public String EmployeeJobGen()
        {
            String job = "";
            if(supervisor.IsChecked == true)
            {
                job = "Supervisor";
            }
            if (picker.IsChecked == true)
            {
                job = "Picker";
            }
            if (stocker.IsChecked == true)
            {
                job = "Stocker";
            }
            return job;
        }
        public String AccIdGenerator()
        {
            String accIdInt = "";
            Random random = new Random();
            for (int i = 0; i < 5; i++)
            {
                accIdInt = accIdInt + random.Next(0, 9).ToString();
            }
            return accIdInt;
        }

        private void TypeSelect_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if(typeSelect.SelectedValue.ToString() == "Employee Account")
            {
                this.employeeTypeIn.Visibility = Visibility.Visible;
            }
        }
    }
}
