using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WebTest.Models;

namespace WebTest.Data
{
    public class TestDBContext : DbContext
    {
        public TestDBContext (DbContextOptions<TestDBContext> options) : base(options)
        {

        }

        public DbSet<Product> product { get; set; }
        public DbSet<MasterAccount> master_account { get; set; }
        public DbSet<CustomerAccount> customer_account { get; set; }
        public DbSet<AccountOrder> order_full { get; set; }
        //public DbSet<OrderItem> order_item { get; set; }

       /* protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<OrderItem>().HasKey(c => { c.order_num, c.prod_id });
        }*/
    }
}
