using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;


namespace WebTest.Models
{
    public class Product
    {
        [Key]
        public int prod_id { get; set; }
        public string prod_name { get; set; }
        public string prod_desc { get; set; }
        public string manu { get; set; }
        public double price { get; set; }
        public int proirity { get; set; }
        public float volume { get; set; }
    }
}
