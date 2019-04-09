using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Threading.Tasks;

namespace WebTest.Models
{
    public class MasterAccount
    {
        [Key]
        public int acc_id { get; set; }
        public int acc_type { get; set; }
        public string username { get; set; }
        public string password { get; set; }
    }
}
