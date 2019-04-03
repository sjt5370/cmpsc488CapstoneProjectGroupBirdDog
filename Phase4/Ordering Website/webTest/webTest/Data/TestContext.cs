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
        {        }
        public DbSet<ProductViewModel> Products { get; set; }
    }
}
