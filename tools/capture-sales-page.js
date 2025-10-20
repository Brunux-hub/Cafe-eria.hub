const { chromium } = require('playwright');

(async () => {
  const browser = await chromium.launch();
  const page = await browser.newPage();
  await page.goto('http://127.0.0.1:8080/admin/sales', { waitUntil: 'networkidle' });
  await page.screenshot({ path: 'sales-page.png', fullPage: true });
  console.log('Screenshot saved: sales-page.png');
  await browser.close();
})();