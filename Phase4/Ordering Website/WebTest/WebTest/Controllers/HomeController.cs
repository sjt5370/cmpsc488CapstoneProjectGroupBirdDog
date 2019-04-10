using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using WebTest.Data;
using WebTest.Models;

namespace WebTest.Controllers
{
    public class HomeController : Controller
    {
        private TestDBContext _dbContext;
        public HomeController(TestDBContext db)
        {
            _dbContext = db;
        }
        public IActionResult Index()
        {
            var products = _dbContext.product.ToList();
            return View(products);
        }

        public IActionResult Login()
        {
            var account = _dbContext.master_account.ToList();
            return View(account);
        }

        public IActionResult AccountInfo()
        {
            var account = _dbContext.customer_account.ToList();
            return View();
        }

       public IActionResult AccountOrderHistory()
        {
            var orders = _dbContext.order_full.ToList();
            return View(orders);
        }

        public IActionResult OrderItems()
        {
            var orders = _dbContext.order_item.ToList();
            return View(orders);
        }

        public IActionResult About()
        {
            ViewData["Message"] = "Your application description page.";

            return View();
        }

        public IActionResult Contact()
        {
            ViewData["Message"] = "Your contact page.";

            return View();
        }

        public IActionResult Privacy()
        {
            return View();
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}
