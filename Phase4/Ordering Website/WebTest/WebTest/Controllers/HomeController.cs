using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
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

        [HttpGet]
        public async Task<IActionResult> IndexAsync()
        {
            var products = await _dbContext.product.ToListAsync();
            return View(products);
        }

        [HttpGet]
        public async Task<IActionResult> LoginAsync()
        {            
            var account = await _dbContext.master_account.ToListAsync();
            return View(account);
        }

        [HttpGet]
        public async Task<IActionResult> AccountInfoAsync()
        {
            var account = await _dbContext.customer_account.Where(x => x.acc_id.Equals(20003)).ToListAsync();
            return View(account);
        }

        [HttpGet]
        public async Task<IActionResult> AccountOrderHistoryAsync()
        {
            var orders = await _dbContext.order_full.Where(x => x.acc_id.Equals(20003)).ToListAsync();
            return View(orders);
        }

        [HttpGet]
        public IActionResult CreateOrder()
        {
            return View();
        }

        [HttpPost]
        public IActionResult CreateOrder(AccountOrder model)
        {
            _dbContext.order_full.Add(model);
            _dbContext.SaveChanges();
            return RedirectToAction("Index");
        }

        [HttpGet]
        public IActionResult CreateOrder2()
        {
            return View();
        }

        [HttpPost]
        public IActionResult CreateOrder2(OrderItem model)
        {            
            _dbContext.order_item.Add(model);
            _dbContext.SaveChanges();
            return RedirectToAction("Index");
        }

        [HttpGet]
        public async Task<IActionResult> OrderItemsAsync()
        {
            var orders = await _dbContext.order_item.ToListAsync();
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
