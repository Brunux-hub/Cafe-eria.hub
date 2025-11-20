-- Actualizar URLs de imágenes de productos con placeholders válidos
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/FFE4B5/000000?text=Latte' WHERE nombre = 'Latte';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/D2691E/FFFFFF?text=Capuccino' WHERE nombre = 'Capuccino';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/FFB6C1/000000?text=Cheesecake' WHERE nombre = 'Cheesecake';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/8B4513/FFFFFF?text=Espresso' WHERE nombre = 'Espresso';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/4682B4/FFFFFF?text=Cappuccino' WHERE nombre = 'Cappuccino';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/FFA500/000000?text=Latte' WHERE nombre LIKE 'Latte%' AND nombre != 'Latte';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/1E90FF/FFFFFF?text=Americano' WHERE nombre = 'Americano';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/CD853F/FFFFFF?text=Mocha' WHERE nombre = 'Mocha';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/87CEEB/000000?text=Iced+Latte' WHERE nombre = 'Iced Latte';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/DEB887/000000?text=Frappuccino' WHERE nombre = 'Frappuccino';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/90EE90/000000?text=Te+Verde' WHERE nombre LIKE 'T%Verde%';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/2F4F4F/FFFFFF?text=Te+Negro' WHERE nombre LIKE 'T%Negro%';
UPDATE producto SET imagen_url = 'https://via.placeholder.com/150/8B4513/FFFFFF?text=Chocolate' WHERE nombre LIKE '%Chocolate%';

-- Verificar las actualizaciones
SELECT id_producto, nombre, imagen_url FROM producto LIMIT 15;
