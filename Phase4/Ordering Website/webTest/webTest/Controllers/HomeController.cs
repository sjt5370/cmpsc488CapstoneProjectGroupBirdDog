using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using webTest.Data;
using webTest.Models;

namespace webTest.Controllers
{
    [Route("api/controller")]
    public class HomeController : Controller
    {
        TestContext _dbcontext;

        public HomeController (TestContext dbcontext)
        {
            _dbcontext = dbcontext;
                _dbcontext.ChangeTracker.QueryTrackingBehavior = QueryTrackingBehavior.NoTracking;
        }

        [HttpGet]
        public async Task<IActionResult> GetAsync()
        {
            var products = await _dbcontext.Products
                .AsNoTracking()
                .ToListAsync();
            return Ok(products);
        }

        [HttpGet("id")]
        public ProductViewModel Get(int id)
        {
            return _dbcontext.Products.Find(id);
        }

        [HttpPost]
        public IActionResult Post([FromBody]ProductViewModel product)
        {
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            _dbcontext.Products.Add(product);
            _dbcontext.SaveChanges();

            return Ok();
        }

        [HttpPut("id")]
        public async Task<IActionResult> PutAsync(int id, [FromBody]ProductViewModel product)
        {

            if (!(_dbcontext.Products.Any(t => t.Id == id)))
            {
                return NotFound();
            }

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            _dbcontext.Products.Update(product);
            await _dbcontext.SaveChangesAsync();

            return Ok();
        }

        [HttpDelete("id")]
        public IActionResult Delete(int id, [FromBody]ProductViewModel product)
        {
            var curProduct = _dbcontext.Products.Find(id);

            if (curProduct == null)
            {
                return NotFound();
            }

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }
            _dbcontext.Products.Remove(product);
            _dbcontext.SaveChanges();

            return NoContent();
        }

        public IActionResult Index()
        {
            return View();
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
