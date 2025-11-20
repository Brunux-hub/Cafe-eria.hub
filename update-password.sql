-- Actualizar contraseña del admin a 'admin123'
UPDATE usuario 
SET contrasena = '$2a$10$JV9DSptDqHHRTfzFgkqKtuBTFoaw5O8A2Kv5Yt2qwotXF0WEmf7ly' 
WHERE correo = 'admin@cafeteriasoma.com';

SELECT id_usuario, nombre, correo, contrasena as hash_bcrypt 
FROM usuario 
WHERE correo = 'admin@cafeteriasoma.com';
