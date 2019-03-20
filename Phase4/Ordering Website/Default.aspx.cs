using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Data.SqlClient;

namespace WMOWNonEmptyTemplate
{
    public partial class _Default : Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            using (SqlConnection connection = new SqlConnection("Data Source=mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com;Initial Catalog=warehouse;User ID=masterUser;Password=master1234;"))
            using (SqlCommand cmd = new SqlCommand("SELECT * FROM product", connection))
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
                            String example = reader.GetString(reader.GetOrdinal("prod_name")) + " " + reader.GetString(reader.GetOrdinal("prod_desc"));
                        }
                    }
                }
                connection.Close();
            }
        }
    }
}