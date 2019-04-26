using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace WebTest.Models
{
    public class AccountOrder
    {
        [Key]
        public int order_num { get; set; }
        public int acc_id { get; set; }
        public Boolean complete { get; set; }
        public int urgency { get; set; }
        public Boolean active { get; set; }
    }
}
