-- INSERST INICIALES PARA LA BASE DE DATOS DE CAFETERÍA SOMA

-- ROLES INICIALES
INSERT INTO rol (nombre, descripcion, activo, fecha_creacion)
VALUES 
('ADMIN', 'Acceso completo al sistema', true, NOW()),
('CLIENTE', 'Acceso limitado, puede comprar y dejar reseñas', true, NOW());


-- USUARIO ADMINISTRADOR
-- Contraseña temporal: admin123 (deberás hashearla más adelante con BCrypt)
INSERT INTO usuario (nombre, correo, contrasena, id_rol, activo, fecha_creacion)
VALUES ('Administrador', 'admin@soma.com', 'admin123', 1, true, NOW());


-- CATEGORÍAS INICIALES
INSERT INTO categoria (nombre, descripcion, activo, fecha_creacion)
VALUES
('Cafés', 'Cafés calientes y fríos', true, NOW()),
('Postres', 'Tortas, muffins y repostería', true, NOW());


-- PRODUCTOS INICIALES
INSERT INTO producto (nombre, descripcion, precio, stock, imagen_url, id_categoria, activo, fecha_creacion)
VALUES
('Latte', 'Café con leche espumada', 10.50, 25, 'https://example.com/img/latte.jpg', 1, true, NOW()),
('Capuccino', 'Café con leche y espuma de leche', 9.80, 30, 'https://example.com/img/capuccino.jpg', 1, true, NOW()),
('Cheesecake', 'Pastel de queso con frutos rojos', 15.00, 10, 'https://example.com/img/cheesecake.jpg', 2, true, NOW());
