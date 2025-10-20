const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = process.env.PORT || 8080;
const DIST_DIR = path.join(__dirname, '..', 'dist', 'cafeteria-soma', 'browser');

function contentTypeByExt(ext) {
  const map = {
    '.html': 'text/html; charset=utf-8',
    '.js': 'application/javascript; charset=utf-8',
    '.css': 'text/css; charset=utf-8',
    '.json': 'application/json; charset=utf-8',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.svg': 'image/svg+xml',
    '.ico': 'image/x-icon',
    '.woff2': 'font/woff2',
  };
  return map[ext.toLowerCase()] || 'application/octet-stream';
}

const server = http.createServer((req, res) => {
  try {
    let reqPath = decodeURIComponent(req.url.split('?')[0]);
    if (reqPath === '/') reqPath = '/index.html';
    let filePath = path.join(DIST_DIR, reqPath);

    // If path points to a directory, fallback to index.html
    if (fs.existsSync(filePath) && fs.statSync(filePath).isDirectory()) {
      filePath = path.join(filePath, 'index.html');
    }

    if (!fs.existsSync(filePath)) {
      // SPA fallback: serve index.html
      filePath = path.join(DIST_DIR, 'index.html');
    }

    const ext = path.extname(filePath) || '.html';
    const ct = contentTypeByExt(ext);
    const stream = fs.createReadStream(filePath);
    res.writeHead(200, { 'Content-Type': ct });
    stream.pipe(res);

    stream.on('error', (err) => {
      res.writeHead(500);
      res.end('Server error');
    });
  } catch (err) {
    res.writeHead(500);
    res.end('Server error');
  }
});

server.listen(PORT, '127.0.0.1', () => {
  console.log(`Static server running at http://127.0.0.1:${PORT}`);
  console.log(`Serving: ${DIST_DIR}`);
});

// Graceful shutdown
process.on('SIGINT', () => {
  console.log('Shutting down');
  server.close(() => process.exit(0));
});
