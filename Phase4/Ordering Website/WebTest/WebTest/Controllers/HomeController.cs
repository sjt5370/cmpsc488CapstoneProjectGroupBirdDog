using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
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
        public IActionResult Login()
        {
            return View();
        }

        [HttpPost]
        public IActionResult Login(MasterAccount model)
        {
            var account = _dbContext.master_account.Where(x => (x.username.Equals(model.username) && x.password.Equals(model.password) && x.acc_type.Equals(true))).ToList();
            if (account.Count()<1)
            {
                ModelState.AddModelError(string.Empty, "Invalid login attempt.");
                return View();
            }
            else
            {
                HttpContext.Session.SetInt32("user_id", account.ElementAt(0).acc_id);
                return RedirectToAction("IndexAsync");
            }
        }

        [HttpGet]
        public async Task<IActionResult> AccountInfoAsync()
        {
            var account = await _dbContext.customer_account.Where(x => x.acc_id.Equals(HttpContext.Session.GetInt32("user_id"))).ToListAsync();
            return View(account);
        }

        [HttpGet]
        public async Task<IActionResult> AccountOrderHistoryAsync()
        {
            var orders = await _dbContext.order_full.Where(x => x.acc_id.Equals(HttpContext.Session.GetInt32("user_id"))).ToListAsync();
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
            model.order_num = _dbContext.order_full.Count() + 1;
            model.acc_id = (int)(HttpContext.Session.GetInt32("user_id"));
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
            var orders = await _dbContext.order_full.Where(x => x.acc_id.Equals(HttpContext.Session.GetInt32("user_id"))).ToListAsync();
            var orderitems = new List<OrderItem>();
            for (int i=0; i<orders.Count; i++)
            {
                var orderitem = await _dbContext.order_item.Where(x => x.order_num.Equals(orders.ElementAt(i).order_num)).ToListAsync();
                orderitems.AddRange(orderitem);
            }
            
            return View(orderitems);
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
