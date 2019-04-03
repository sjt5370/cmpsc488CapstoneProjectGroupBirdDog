using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;
using Microsoft.IdentityModel.Protocols;

namespace webTest.Models
{
    public class Helper
    {
        public static string GetRDSConnectionString()
        {
            string dbname = "warehouse";

            if (string.IsNullOrEmpty(dbname)) return null;

            string username = "masterUser";
            string password = "master1234";
            string hostname = "mycsdb.civb68g6fy4p.us-east-2.rds.amazonaws.com";
            string port = "1433";

            return "Data Source=" + hostname + ";Initial Catalog=" + dbname + ";User ID=" + username + ";Password=" + password + ";";
        }
    }
}
