# Cafetería Soma - Backend API

## 🚀 Stack Tecnológico
- **Java 21**
- **Spring Boot 3.5.7**
- **Spring Security + JWT**
- **MySQL 8**
- **JPA/Hibernate**
- **Lombok**
- **Maven**

## 📋 Requisitos Previos
- JDK 21 instalado
- MySQL 8.0+ corriendo en localhost:3306
- Maven 3.6+

## 🔧 Configuración

### 1. Base de Datos
Crea la base de datos en MySQL:
```sql
CREATE DATABASE cafeteria_soma CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configuración de application.properties
Edita `src/main/resources/application.properties` y actualiza las credenciales de MySQL:
```properties
spring.datasource.username=root
spring.datasource.password=TU_CONTRASEÑA
```

### 3. Datos Iniciales (Opcional)
El sistema creará automáticamente las tablas. Para tener datos de prueba, ejecuta este script SQL después del primer arranque:

```sql
-- Insertar roles
INSERT INTO rol (nombre, descripcion, activo, fecha_creacion) VALUES
('ADMIN', 'Administrador del sistema', true, NOW()),
('CLIENT', 'Cliente de la cafetería', true, NOW());

-- Insertar usuario administrador (contraseña: admin123)
INSERT INTO usuario (nombre, correo, contrasena, id_rol, activo, fecha_creacion) VALUES
('Administrador', 'admin@cafeteriasoma.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 1, true, NOW());

-- Insertar categorías
INSERT INTO categoria (nombre, descripcion, activo, fecha_creacion) VALUES
('Café Caliente', 'Variedades de café caliente', true, NOW()),
('Bebidas Frías', 'Bebidas heladas y refrescantes', true, NOW()),
('Bebidas Calientes', 'Tés e infusiones', true, NOW()),
('Postres', 'Postres y dulces', true, NOW()),
('Snacks', 'Snacks y bocadillos', true, NOW());

-- Insertar productos de ejemplo
INSERT INTO producto (nombre, descripcion, precio, stock, imagen_url, id_categoria, activo, fecha_creacion) VALUES
('Espresso', 'Café espresso italiano tradicional', 3.50, 100, 'assets/images/espresso.svg', 1, true, NOW()),
('Cappuccino', 'Espresso con leche vaporizada y espuma', 4.50, 100, 'assets/images/cappuccino.svg', 1, true, NOW()),
('Latte', 'Café con leche y arte latte', 4.75, 100, 'assets/images/latte.svg', 1, true, NOW()),
('Americano', 'Espresso diluido con agua caliente', 3.75, 100, 'assets/images/americano.svg', 1, true, NOW()),
('Té Verde', 'Té verde premium japonés', 3.25, 80, 'assets/images/te-verde.svg', 3, true, NOW());
```

## 🏃 Ejecutar la Aplicación

### Opción 1: Con Maven
```bash
cd cafeteria-soma-backend
mvn clean install
mvn spring-boot:run
```

### Opción 2: Con IDE (IntelliJ/Eclipse)
1. Importa el proyecto como proyecto Maven
2. Ejecuta la clase `CafeteriaSomaApplication.java`

La API estará disponible en: **http://localhost:8080**

## 🔐 Endpoints de la API

### Autenticación (Público)
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login de usuario |
| POST | `/api/auth/register` | Registro de nuevo cliente |

#### Login Request:
```json
{
  "username": "admin@cafeteriasoma.com",
  "password": "admin123"
}
```

#### Register Request:
```json
{
  "fullName": "Juan Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "phone": "555-1234",
  "address": "Calle Principal 123"
}
```

#### Auth Response:
```json
{
  "user": {
    "id": "1",
    "username": "admin@cafeteriasoma.com",
    "email": "admin@cafeteriasoma.com",
    "fullName": "Administrador",
    "role": "ADMIN",
    "createdAt": "2024-01-01T10:00:00"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Productos
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/productos` | Listar todos los productos | Público |
| GET | `/api/productos/{id}` | Obtener producto por ID | Público |
| POST | `/api/productos` | Crear producto | Admin |
| PUT | `/api/productos/{id}` | Actualizar producto | Admin |
| DELETE | `/api/productos/{id}` | Eliminar producto | Admin |

### Promociones
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/promociones` | Listar promociones | Auth |
| GET | `/api/promociones/activas` | Promociones activas | Público |
| POST | `/api/promociones` | Crear promoción | Admin |
| PUT | `/api/promociones/{id}` | Actualizar promoción | Admin |
| DELETE | `/api/promociones/{id}` | Eliminar promoción | Admin |

### Ventas
| Método | Endpoint | Descripción | Auth |
|--------|----------|-------------|------|
| GET | `/api/ventas` | Listar ventas | Auth |
| GET | `/api/ventas/{id}` | Obtener venta por ID | Auth |
| POST | `/api/ventas` | Crear venta | Auth |
| GET | `/api/ventas/stats` | Estadísticas de ventas | Admin |

## 🔑 Autenticación JWT

Todas las rutas protegidas requieren un token JWT en el header:
```
Authorization: Bearer <token>
```

El token se obtiene en el login y tiene una validez de 24 horas.

## 🛡️ Seguridad
- Contraseñas encriptadas con BCrypt
- Tokens JWT firmados con HS256
- CORS configurado para desarrollo (localhost:4200)
- Sesiones stateless
- Roles y permisos con Spring Security

## 📝 Estructura del Proyecto
```
src/main/java/com/cafeteriasoma/app/
├── controller/          # REST Controllers
├── dto/                 # Data Transfer Objects
│   ├── auth/           # DTOs de autenticación
│   └── producto/       # DTOs de productos
├── entity/             # Entidades JPA
├── repository/         # Repositorios JPA
├── security/           # Configuración JWT y Security
├── service/            # Lógica de negocio
└── CafeteriaSomaApplication.java
```

## 🐛 Troubleshooting

### Error: "Access denied for user"
- Verifica las credenciales de MySQL en `application.properties`

### Error: "Table doesn't exist"
- Asegúrate de que `spring.jpa.hibernate.ddl-auto=update` esté activo
- Verifica que la base de datos `cafeteria_soma` exista

### Error: "Port 8080 already in use"
- Cambia el puerto en `application.properties`: `server.port=8081`

## 📧 Contacto
Para dudas o soporte: [Agregar información de contacto]
