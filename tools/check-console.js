const puppeteer = require('puppeteer');

(async ()=>{
  const url = process.env.URL || 'http://127.0.0.1:8080/login';
  try{
    const browser = await puppeteer.launch({args:['--no-sandbox','--disable-setuid-sandbox']});
    const page = await browser.newPage();
    const logs = [];
    page.on('console', msg => logs.push({type: msg.type(), text: msg.text()}));
    page.on('pageerror', err => logs.push({type: 'pageerror', text: err.message}));

    await page.goto(url, {waitUntil:'networkidle2', timeout:30000});
    await new Promise(r=>setTimeout(r,1000));
    console.log('---CONSOLE LOGS START---');
    logs.forEach(l => console.log(`[${l.type}] ${l.text}`));
    console.log('---CONSOLE LOGS END---');
    await browser.close();
    process.exit(0);
  }catch(e){
    console.error('ERR', e);
    process.exit(2);
  }
})();
