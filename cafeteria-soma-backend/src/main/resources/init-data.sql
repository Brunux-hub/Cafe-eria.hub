-- =====================================================
-- SCRIPT DE INICIALIZACIÓN - CAFETERÍA SOMA
-- =====================================================
-- Este script inserta datos iniciales para probar la aplicación.
-- Ejecutar DESPUÉS de que Spring Boot haya creado las tablas.
-- =====================================================

USE cafeteria_soma;

-- =====================================================
-- 1. ROLES
-- =====================================================
INSERT INTO rol (nombre, descripcion, activo, fecha_creacion, fecha_actualizacion) VALUES
('ADMIN', 'Administrador del sistema con todos los permisos', true, NOW(), NOW()),
('CLIENT', 'Cliente de la cafetería', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion);

-- =====================================================
-- 2. USUARIOS
-- =====================================================
-- Contraseña para admin: admin123 (encriptada con BCrypt)
-- Contraseña para cliente: password123
INSERT INTO usuario (nombre, correo, contrasena, id_rol, activo, fecha_creacion, fecha_actualizacion) VALUES
('Administrador', 'admin@cafeteriasoma.com', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
 (SELECT id_rol FROM rol WHERE nombre = 'ADMIN'), true, NOW(), NOW()),
('Juan Pérez', 'juan@example.com', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
 (SELECT id_rol FROM rol WHERE nombre = 'CLIENT'), true, NOW(), NOW()),
('María López', 'maria@example.com', 
 '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
 (SELECT id_rol FROM rol WHERE nombre = 'CLIENT'), true, NOW(), NOW())
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- =====================================================
-- 3. CATEGORÍAS
-- =====================================================
INSERT INTO categoria (nombre, descripcion, activo, fecha_creacion, fecha_actualizacion) VALUES
('Café Caliente', 'Variedades de café caliente preparado con granos premium', true, NOW(), NOW()),
('Café Frío', 'Bebidas de café heladas y refrescantes', true, NOW(), NOW()),
('Bebidas Calientes', 'Tés, infusiones y chocolate caliente', true, NOW(), NOW()),
('Bebidas Frías', 'Jugos, smoothies y bebidas refrescantes', true, NOW(), NOW()),
('Postres', 'Postres, pasteles y dulces artesanales', true, NOW(), NOW()),
('Snacks', 'Snacks salados y bocadillos', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion);

-- =====================================================
-- 4. PRODUCTOS
-- =====================================================
INSERT INTO producto (nombre, descripcion, precio, stock, imagen_url, id_categoria, activo, fecha_creacion, fecha_actualizacion) VALUES
-- Cafés Calientes
('Espresso', 'Café espresso italiano tradicional de 30ml', 3.50, 100, 'assets/images/espresso.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Café Caliente' LIMIT 1), true, NOW(), NOW()),
('Cappuccino', 'Espresso con leche vaporizada y espuma cremosa', 4.50, 100, 'assets/images/cappuccino.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Café Caliente' LIMIT 1), true, NOW(), NOW()),
('Latte', 'Café con leche y arte latte personalizado', 4.75, 100, 'assets/images/latte.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Café Caliente' LIMIT 1), true, NOW(), NOW()),
('Americano', 'Espresso diluido con agua caliente', 3.75, 100, 'assets/images/americano.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Café Caliente' LIMIT 1), true, NOW(), NOW()),
('Mocha', 'Espresso con chocolate y leche vaporizada', 5.00, 80, 'assets/images/mocha.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Café Caliente' LIMIT 1), true, NOW(), NOW()),

-- Cafés Fríos
('Iced Latte', 'Latte servido con hielo y leche fría', 5.00, 80, 'assets/images/iced-latte.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Café Frío' LIMIT 1), true, NOW(), NOW()),
('Frappuccino', 'Café batido con hielo y crema', 5.50, 70, 'assets/images/frappuccino.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Café Frío' LIMIT 1), true, NOW(), NOW()),

-- Bebidas Calientes
('Té Verde', 'Té verde premium japonés', 3.25, 80, 'assets/images/te-verde.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Bebidas Calientes' LIMIT 1), true, NOW(), NOW()),
('Té Negro', 'Té negro inglés tradicional', 3.00, 80, 'assets/images/te-negro.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Bebidas Calientes' LIMIT 1), true, NOW(), NOW()),
('Chocolate Caliente', 'Chocolate belga con leche vaporizada', 4.25, 60, 'assets/images/chocolate.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Bebidas Calientes' LIMIT 1), true, NOW(), NOW()),

-- Postres
('Cheesecake', 'Cheesecake de fresa con base de galleta', 6.50, 30, 'assets/images/cheesecake.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Postres' LIMIT 1), true, NOW(), NOW()),
('Brownie', 'Brownie de chocolate con nueces', 4.00, 40, 'assets/images/brownie.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Postres' LIMIT 1), true, NOW(), NOW()),
('Croissant', 'Croissant francés de mantequilla', 3.50, 50, 'assets/images/croissant.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Postres' LIMIT 1), true, NOW(), NOW()),

-- Snacks
('Sándwich Club', 'Sándwich triple con pollo, bacon y vegetales', 7.50, 25, 'assets/images/sandwich.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Snacks' LIMIT 1), true, NOW(), NOW()),
('Wrap Vegetariano', 'Wrap integral con vegetales asados', 6.00, 30, 'assets/images/wrap.svg', 
 (SELECT id_categoria FROM categoria WHERE nombre = 'Snacks' LIMIT 1), true, NOW(), NOW())
ON DUPLICATE KEY UPDATE precio=VALUES(precio), stock=VALUES(stock);

-- =====================================================
-- 5. PROMOCIONES
-- =====================================================
INSERT INTO promocion (nombre, descripcion, porcentaje_descuento, fecha_inicio, fecha_fin, activo, fecha_creacion, fecha_actualizacion) VALUES
('Descuento Cafés Calientes', '20% de descuento en todos los cafés calientes', 20.00, DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 30 DAY), true, NOW(), NOW()),
('Promo Postres', '15% de descuento en postres seleccionados', 15.00, DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_ADD(NOW(), INTERVAL 15 DAY), true, NOW(), NOW()),
('Happy Hour', '25% en bebidas de 14:00 a 16:00', 25.00, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), true, NOW(), NOW())
ON DUPLICATE KEY UPDATE porcentaje_descuento=VALUES(porcentaje_descuento);

-- =====================================================
-- 6. RELACIÓN PRODUCTOS-PROMOCIONES
-- =====================================================
-- Asignar cafés a la promoción de cafés calientes
INSERT IGNORE INTO producto_promocion (id_promocion, id_producto)
SELECT 
    (SELECT id_promocion FROM promocion WHERE nombre = 'Descuento Cafés Calientes' LIMIT 1),
    id_producto
FROM producto
WHERE id_categoria = (SELECT id_categoria FROM categoria WHERE nombre = 'Café Caliente' LIMIT 1);

-- Asignar postres a la promoción de postres
INSERT IGNORE INTO producto_promocion (id_promocion, id_producto)
SELECT 
    (SELECT id_promocion FROM promocion WHERE nombre = 'Promo Postres' LIMIT 1),
    id_producto
FROM producto
WHERE id_categoria = (SELECT id_categoria FROM categoria WHERE nombre = 'Postres' LIMIT 1)
LIMIT 2;

-- =====================================================
-- 7. VENTAS DE EJEMPLO
-- =====================================================
INSERT INTO venta (id_usuario, fecha_venta, total, metodo_pago, estado, voucher_url) VALUES
((SELECT id_usuario FROM usuario WHERE correo = 'juan@example.com'), DATE_SUB(NOW(), INTERVAL 5 DAY), 14.40, 'Tarjeta', 'COMPLETADO', null),
((SELECT id_usuario FROM usuario WHERE correo = 'maria@example.com'), DATE_SUB(NOW(), INTERVAL 3 DAY), 11.20, 'Efectivo', 'COMPLETADO', null),
((SELECT id_usuario FROM usuario WHERE correo = 'juan@example.com'), DATE_SUB(NOW(), INTERVAL 1 DAY), 18.50, 'Yape', 'COMPLETADO', null)
ON DUPLICATE KEY UPDATE total=VALUES(total);

-- =====================================================
-- 8. DETALLES DE VENTAS
-- =====================================================
-- Venta 1: Juan - 2 Espressos + 2 Cappuccinos
INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES
((SELECT id_venta FROM venta WHERE id_usuario = (SELECT id_usuario FROM usuario WHERE correo = 'juan@example.com') ORDER BY fecha_venta LIMIT 1),
 (SELECT id_producto FROM producto WHERE nombre = 'Espresso' LIMIT 1), 2, 3.50, 7.00),
((SELECT id_venta FROM venta WHERE id_usuario = (SELECT id_usuario FROM usuario WHERE correo = 'juan@example.com') ORDER BY fecha_venta LIMIT 1),
 (SELECT id_producto FROM producto WHERE nombre = 'Cappuccino' LIMIT 1), 2, 4.50, 9.00)
ON DUPLICATE KEY UPDATE cantidad=VALUES(cantidad);

-- Venta 2: María - 2 Lattes + 1 Té Verde
INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES
((SELECT id_venta FROM venta WHERE id_usuario = (SELECT id_usuario FROM usuario WHERE correo = 'maria@example.com') ORDER BY fecha_venta LIMIT 1),
 (SELECT id_producto FROM producto WHERE nombre = 'Latte' LIMIT 1), 2, 4.75, 9.50),
((SELECT id_venta FROM venta WHERE id_usuario = (SELECT id_usuario FROM usuario WHERE correo = 'maria@example.com') ORDER BY fecha_venta LIMIT 1),
 (SELECT id_producto FROM producto WHERE nombre = 'Té Verde' LIMIT 1), 1, 3.25, 3.25)
ON DUPLICATE KEY UPDATE cantidad=VALUES(cantidad);

-- =====================================================
-- VERIFICACIÓN
-- =====================================================
-- Contar registros insertados
SELECT 'Roles creados:' as Info, COUNT(*) as Total FROM rol;
SELECT 'Usuarios creados:' as Info, COUNT(*) as Total FROM usuario;
SELECT 'Categorías creadas:' as Info, COUNT(*) as Total FROM categoria;
SELECT 'Productos creados:' as Info, COUNT(*) as Total FROM producto;
SELECT 'Promociones creadas:' as Info, COUNT(*) as Total FROM promocion;
SELECT 'Ventas creadas:' as Info, COUNT(*) as Total FROM venta;
SELECT 'Detalles de venta creados:' as Info, COUNT(*) as Total FROM detalle_venta;

-- =====================================================
-- CREDENCIALES DE PRUEBA
-- =====================================================
/*
USUARIOS DE PRUEBA:
1. Admin:
   - Email: admin@cafeteriasoma.com
   - Password: admin123
   
2. Cliente 1:
   - Email: juan@example.com
   - Password: password123
   
3. Cliente 2:
   - Email: maria@example.com
   - Password: password123

NOTA: Todas las contraseñas están encriptadas con BCrypt.
*/

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
