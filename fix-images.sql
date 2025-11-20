-- Actualizar URLs de imágenes de productos con placeholders válidos usando placehold.co
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/FFE4B5/000000?text=Latte' WHERE nombre = 'Latte';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/D2691E/FFFFFF?text=Capuccino' WHERE nombre = 'Capuccino';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/FFB6C1/000000?text=Cheesecake' WHERE nombre = 'Cheesecake';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/8B4513/FFFFFF?text=Espresso' WHERE nombre = 'Espresso';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/4682B4/FFFFFF?text=Cappuccino' WHERE nombre = 'Cappuccino';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/FFA500/000000?text=Latte' WHERE nombre LIKE 'Latte%' AND nombre != 'Latte';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/1E90FF/FFFFFF?text=Americano' WHERE nombre = 'Americano';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/CD853F/FFFFFF?text=Mocha' WHERE nombre = 'Mocha';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/87CEEB/000000?text=Iced+Latte' WHERE nombre = 'Iced Latte';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/DEB887/000000?text=Frappuccino' WHERE nombre = 'Frappuccino';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/90EE90/000000?text=Te+Verde' WHERE nombre LIKE 'T%Verde%';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/2F4F4F/FFFFFF?text=Te+Negro' WHERE nombre LIKE 'T%Negro%';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/8B4513/FFFFFF?text=Chocolate' WHERE nombre LIKE '%Chocolate%';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/8B4513/FFFFFF?text=Brownie' WHERE nombre = 'Brownie';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/FFD700/000000?text=Croissant' WHERE nombre = 'Croissant';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/F4A460/FFFFFF?text=Sandwich' WHERE nombre LIKE '%ndwich%';
UPDATE producto SET imagen_url = 'https://placehold.co/150x150/9ACD32/FFFFFF?text=Wrap' WHERE nombre LIKE '%Wrap%';

-- Actualizar todos los que tengan rutas locales o via.placeholder
UPDATE producto SET imagen_url = CONCAT('https://placehold.co/150x150/CCCCCC/000000?text=', REPLACE(nombre, ' ', '+')) 
WHERE imagen_url LIKE '%assets%' OR imagen_url LIKE 'http://localhost%' OR imagen_url LIKE '%via.placeholder%';

-- Verificar las actualizaciones
SELECT id_producto, nombre, imagen_url FROM producto;
