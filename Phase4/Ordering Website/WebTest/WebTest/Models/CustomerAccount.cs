using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;


namespace WebTest.Models
{
    public class CustomerAccount
    {
        [Key]
        public int acc_id { get; set; }
        public string email { get; set; }
        public string cus_name { get; set; }
        public string addr_street { get; set; }
        public string addr_city { get; set; }
        public string addr_state { get; set; }
        public int addr_zip { get; set; }
    }
}
