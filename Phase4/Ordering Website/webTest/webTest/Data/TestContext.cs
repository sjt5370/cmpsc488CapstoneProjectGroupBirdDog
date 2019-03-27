using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;
using webTest.Models;

namespace webTest.Data
{
    public class TestContext:DbContext
    {
        public TestContext(DbContextOptions<TestContext> dbContextOptions)
        {
            this.ChangeTracker.QueryTrackingBehavior=QueryTrackingBehavior.NoTracking;
        }
        public DbSet<ProductViewModel> Products { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            //modelBuilder.Entity<ProductViewModel>().HasKey(t => t.Id);
        }
    }
}
