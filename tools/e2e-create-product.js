const { chromium } = require('playwright');
const spawn = require('child_process').spawn;

(async () => {
  const server = spawn('node', ['tools/static-server.js'], { stdio: 'inherit' });
  // wait a bit for server
  await new Promise(r => setTimeout(r, 1000));

  const browser = await chromium.launch();
  const page = await browser.newPage();
  page.on('console', msg => console.log('PAGE LOG:', msg.text()));
  page.on('pageerror', err => console.log('PAGE ERROR:', err.message));

  try {
    await page.goto('http://127.0.0.1:8080/login', { waitUntil: 'networkidle' });

    // login - use input selectors from your login component
    await page.fill('#username', 'admin');
    await page.fill('#password', 'admin123');
    await page.click('button[type=submit]');

  // wait navigation to admin area
  await page.waitForURL('**/admin/**', { timeout: 5000 });

  // go to products page to see the list
  await page.goto('http://127.0.0.1:8080/admin/products', { waitUntil: 'networkidle' });
  await page.waitForSelector('text=Gestión de Productos', { timeout: 5000 }).catch(()=>{});

  // open modal
  await page.click('button:has-text("Nuevo Producto")');
    await page.fill('input[formcontrolname="name"]', 'Producto E2E');
    await page.fill('input[formcontrolname="price"]', '9.99');
    await page.fill('textarea[formcontrolname="description"]', 'Creado por E2E');
    await page.selectOption('select[formcontrolname="category"]', 'Café Caliente');
    await page.fill('input[formcontrolname="stock"]', '10');
    await page.click('button:has-text("Crear")');

  // wait for modal to close (form submit) or for modal to disappear
  await page.waitForSelector('button:has-text("Crear")', { state: 'detached', timeout: 5000 }).catch(()=>{});
    // give some time and dump page HTML for debugging
    await page.waitForTimeout(500);
    const html = await page.content();
    console.log('PAGE HTML SNIPPET:\n', html.slice(0, 2000));
    // assert product name exists
    const found = await page.$(`text=Producto E2E`);
    if (found) {
      console.log('E2E: Product created and visible');
    } else {
      throw new Error('Product not found in page after creation');
    }

    await browser.close();
  } catch (err) {
    console.error('E2E error:', err);
    await browser.close();
    process.exit(2);
  } finally {
    server.kill();
  }

  process.exit(0);
})();
