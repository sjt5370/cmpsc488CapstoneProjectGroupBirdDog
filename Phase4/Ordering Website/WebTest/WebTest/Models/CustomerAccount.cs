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
        [Required]
        public string email { get; set; }
        [Required]
        public string cus_name { get; set; }
        [Required]
        public string addr_street { get; set; }
        [Required]
        public string addr_city { get; set; }
        [Required]
        public string addr_state { get; set; }
        [Required]
        public int addr_zip { get; set; }
    }
}
