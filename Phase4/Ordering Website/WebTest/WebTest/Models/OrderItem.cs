using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace WebTest.Models
{
    public class OrderItem
    {
        [Key]
        public int order_num { get; set; }
        [Key]
        public int prod_id { get; set; }
        public int quantity { get; set; }
    }
}
