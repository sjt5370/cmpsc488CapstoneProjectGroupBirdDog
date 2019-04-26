using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using WebTest.Data;
using WebTest.Helpers;
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
        public async Task<IActionResult> OrderItemsAsync()
        {
            var orders = await _dbContext.order_full.Where(x => x.acc_id.Equals(HttpContext.Session.GetInt32("user_id"))).ToListAsync();
            var orderitems = new List<OrderItem>();
            for (int i = 0; i < orders.Count; i++)
            {
                var orderitem = await _dbContext.order_item.Where(x => x.order_num.Equals(orders.ElementAt(i).order_num)).ToListAsync();
                orderitems.AddRange(orderitem);
            }

            return View(orderitems);
        }

        public IActionResult Cart()
        {
            if (SessionHelper.GetObjectFromJson<List<OrderItem>>(HttpContext.Session, "cart") == null)
            {
                List<OrderItem> cart = new List<OrderItem>();                
                SessionHelper.SetObjectAsJson(HttpContext.Session, "cart", cart);
                ViewBag.cart = cart;
                ViewBag.total = 0;
            }
            else
            {
                var cart = SessionHelper.GetObjectFromJson<List<OrderItem>>(HttpContext.Session, "cart");                
                ViewBag.cart = cart;
                ViewBag.total = cart.Sum(item => _dbContext.product.Find(item.prod_id).price * item.quantity);
            }            
            
            return View();
        }

        [HttpGet]
        public IActionResult CreateOrder()
        {
            return View();
        }
        
        [HttpPost]
        public IActionResult CreateOrder(MasterAccount model)
        {
            var account = _dbContext.master_account.Where(x => (x.username.Equals(model.username) && x.password.Equals(model.password) && x.acc_type.Equals(true))).ToList();
            if (account.Count() < 1 || HttpContext.Session.GetInt32("user_id") != account.ElementAt(0).acc_id)
            {
                ModelState.AddModelError(string.Empty, "Invalid validation attempt.");
                return View();
            }
            else
            {
                AccountOrder model1 = new AccountOrder();
                model1.order_num = _dbContext.order_full.Count() + 1;
                model1.acc_id = (int)(HttpContext.Session.GetInt32("user_id"));
                model1.urgency = 10;
                model1.active = true;
                _dbContext.order_full.Add(model1);

                List<OrderItem> cart = SessionHelper.GetObjectFromJson<List<OrderItem>>(HttpContext.Session, "cart");
                for (int i = 0; i < cart.Count(); i++)
                {
                    OrderItem model2 = new OrderItem();
                    model2.order_num = model1.order_num;
                    model2.prod_id = cart.ElementAt(i).prod_id;
                    model2.quantity = cart.ElementAt(i).quantity;
                    _dbContext.order_item.Add(model2);
                }

                _dbContext.SaveChanges();

                List<OrderItem> ResetCart = new List<OrderItem>();
                SessionHelper.SetObjectAsJson(HttpContext.Session, "cart", ResetCart);
                ViewBag.cart = ResetCart;

                return RedirectToAction("IndexAsync");
            }
        }

        [HttpGet]
        public IActionResult AddToCart()
        {
            return View();
        }

        [HttpPost]
        public IActionResult AddToCart(OrderItem model)
        {
            model.order_num = 0;
            if (SessionHelper.GetObjectFromJson<List<OrderItem>>(HttpContext.Session, "cart") == null)
            {
                List<OrderItem> cart = new List<OrderItem>();
                cart.Add(model);
                SessionHelper.SetObjectAsJson(HttpContext.Session, "cart", cart);
            }
            else
            {
                List<OrderItem> cart = SessionHelper.GetObjectFromJson<List<OrderItem>>(HttpContext.Session, "cart");
                cart.Add(model);
                SessionHelper.SetObjectAsJson(HttpContext.Session, "cart", cart);
            }
            return RedirectToAction("Cart");
        }

        [Route("remove/{id}")]
        public IActionResult RemoveFromCart(int prodId)
        {
            List<OrderItem> cart = SessionHelper.GetObjectFromJson<List<OrderItem>>(HttpContext.Session, "cart");
            int index = FindElement(prodId);
            cart.RemoveAt(index);
            SessionHelper.SetObjectAsJson(HttpContext.Session, "cart", cart);
            return RedirectToAction("Cart");
        }

        private int FindElement(int id)
        {
            List<OrderItem> cart = SessionHelper.GetObjectFromJson<List<OrderItem>>(HttpContext.Session, "cart");
            for (int i = 0; i < cart.Count(); i++)
            {
                if (cart.ElementAt(i).prod_id.Equals(id))
                {
                    return i;
                }
            }
            return -1;
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
