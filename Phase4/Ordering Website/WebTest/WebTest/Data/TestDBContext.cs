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
    }
}
