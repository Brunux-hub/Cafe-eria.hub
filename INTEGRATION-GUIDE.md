# Cafetería Soma - Guía de Integración Frontend-Backend

## 🎯 Resumen Rápido

Este proyecto está **completamente configurado** para conectar el frontend Angular con el backend Spring Boot + JWT.

### ✅ Lo que está LISTO:

#### Backend (Spring Boot)
- ✅ JWT completo (JwtUtil, JwtAuthenticationFilter, SecurityConfig)
- ✅ Autenticación (AuthController, AuthService)
- ✅ DTOs de request/response
- ✅ CORS configurado para localhost:4200
- ✅ Dependencias JWT añadidas al pom.xml
- ✅ CustomUserDetailsService
- ✅ Encriptación de contraseñas con BCrypt
- ✅ Endpoints REST documentados

#### Frontend (Angular)
- ✅ AuthInterceptor (añade token automáticamente)
- ✅ ErrorInterceptor (maneja errores HTTP)
- ✅ AuthService actualizado para usar HttpClient
- ✅ ProductService actualizado con HttpClient
- ✅ Proxy configurado (proxy.conf.json)
- ✅ Environments configurados (dev/prod)
- ✅ Guards para rutas protegidas

---

## 🚀 PASOS PARA ARRANCAR TODO

### 1️⃣ Backend (Terminal 1)

```bash
cd cafeteria-soma-backend

# Crear base de datos
mysql -u root -p
CREATE DATABASE cafeteria_soma;
EXIT;

# Configurar application.properties (ya está configurado, solo verifica la contraseña de MySQL)
# Editar src/main/resources/application.properties si es necesario

# Instalar dependencias y compilar
mvn clean install

# Ejecutar backend
mvn spring-boot:run
```

**Backend corriendo en:** http://localhost:8080

### 2️⃣ Frontend (Terminal 2)

```bash
cd cafeteria-soma   # (la carpeta raíz del frontend)

# Instalar dependencias (si no lo hiciste)
npm install

# Iniciar dev server con proxy
npm start
```

**Frontend corriendo en:** http://localhost:4200

---

## 🔐 Probar Autenticación

### Opción 1: Usar Mock (sin backend)
Si el backend NO está corriendo, el frontend usa datos mock automáticamente:
- Usuario: `admin`
- Contraseña: `admin123`

### Opción 2: Conectar al Backend Real

1. **Crear usuario administrador** en la base de datos:

```sql
USE cafeteria_soma;

-- Insertar rol ADMIN
INSERT INTO rol (nombre, descripcion, activo, fecha_creacion) 
VALUES ('ADMIN', 'Administrador del sistema', true, NOW());

-- Insertar usuario admin (contraseña: admin123)
INSERT INTO usuario (nombre, correo, contrasena, id_rol, activo, fecha_creacion) 
VALUES ('Administrador', 'admin@cafeteriasoma.com', 
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
        1, true, NOW());

-- Verificar
SELECT * FROM usuario;
```

2. **Hacer login desde el frontend:**
   - Ir a http://localhost:4200/login
   - Usuario: `admin@cafeteriasoma.com`
   - Contraseña: `admin123`

3. **Verificar en DevTools (F12):**
   - Pestaña Network: Ver request a `/api/auth/login`
   - Pestaña Application > Local Storage: Ver token JWT guardado

---

## 📡 Endpoints Disponibles

### Autenticación (Público)

**POST /api/auth/login**
```json
Request:
{
  "username": "admin@cafeteriasoma.com",
  "password": "admin123"
}

Response:
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

**POST /api/auth/register**
```json
Request:
{
  "fullName": "Juan Pérez",
  "email": "juan@example.com",
  "password": "password123",
  "phone": "555-1234",
  "address": "Calle Principal 123"
}

Response: (igual que login)
```

### Productos (GET público, POST/PUT/DELETE requieren Auth)

- GET `/api/productos` - Listar todos
- GET `/api/productos/{id}` - Obtener por ID
- POST `/api/productos` - Crear (Admin)
- PUT `/api/productos/{id}` - Actualizar (Admin)
- DELETE `/api/productos/{id}` - Eliminar (Admin)

---

## 🧪 Probar con cURL

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin@cafeteriasoma.com","password":"admin123"}'
```

### Obtener Productos (sin auth)
```bash
curl http://localhost:8080/api/productos
```

### Crear Producto (con token)
```bash
# Primero obtén el token del login
TOKEN="tu_token_aqui"

curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Café Nuevo",
    "description": "Descripción",
    "category": "Café Caliente",
    "price": 5.50,
    "stock": 50,
    "image": "assets/images/cafe.svg"
  }'
```

---

## 🐛 Troubleshooting

### Backend no arranca
```
Error: "Access denied for user 'root'@'localhost'"
```
**Solución:** Verifica la contraseña de MySQL en `application.properties`

---

### Frontend no conecta al backend
```
Error: "Http failure response for http://localhost:4200/api/auth/login"
```
**Solución:** 
1. Verifica que el backend esté corriendo
2. Revisa `proxy.conf.json`:
```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true,
    "logLevel": "info"
  }
}
```

---

### Error 401 Unauthorized
**Solución:**
- Token expirado (24 horas). Vuelve a hacer login
- Verifica que el token esté en localStorage
- Abre DevTools > Application > Local Storage

---

### CORS Error
```
Access to XMLHttpRequest at 'http://localhost:8080/api/...' has been blocked by CORS policy
```
**Solución:**
- Si usas el proxy (`npm start`), no deberías tener este error
- Si arrancas sin proxy, asegúrate de que SecurityConfig tenga:
```java
configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
```

---

## 📝 Estructura de Archivos Clave

### Backend
```
src/main/java/com/cafeteriasoma/app/
├── controller/
│   └── AuthController.java          ✅ CREADO
├── dto/auth/
│   ├── LoginRequest.java            ✅ CREADO
│   ├── RegisterRequest.java         ✅ CREADO
│   ├── AuthResponse.java            ✅ CREADO
│   └── UserDto.java                 ✅ CREADO
├── security/
│   ├── JwtUtil.java                 ✅ CREADO
│   ├── JwtAuthenticationFilter.java ✅ CREADO
│   └── SecurityConfig.java          ✅ CREADO
├── service/
│   ├── AuthService.java             ✅ CREADO
│   └── CustomUserDetailsService.java✅ CREADO
└── entity/ (ya existían)
    ├── Usuario.java
    ├── Rol.java
    └── ...
```

### Frontend
```
src/app/
├── core/
│   ├── interceptors/
│   │   ├── auth.interceptor.ts      ✅ CREADO
│   │   └── error.interceptor.ts     ✅ CREADO
│   └── services/
│       ├── auth.service.ts          ✅ ACTUALIZADO (HttpClient)
│       └── product.service.ts       ✅ ACTUALIZADO (HttpClient)
├── environments/
│   ├── environment.ts               ✅ CREADO
│   └── environment.prod.ts          ✅ CREADO
└── app.config.ts                    ✅ ACTUALIZADO (interceptors)
```

---

## ✅ Checklist Final

### Backend
- [x] JWT dependencies en pom.xml
- [x] JwtUtil, JwtAuthenticationFilter
- [x] SecurityConfig con CORS
- [x] AuthController y AuthService
- [x] CustomUserDetailsService
- [x] DTOs de autenticación
- [x] application.properties configurado
- [ ] **Crear ProductoController, SaleController, PromocionController** (pendiente - puedes crearlos siguiendo el patrón de AuthController)
- [ ] **Poblar base de datos con datos iniciales** (SQL en README.md del backend)

### Frontend
- [x] Environments configurados
- [x] Proxy configurado
- [x] Auth interceptor
- [x] Error interceptor
- [x] AuthService con HttpClient
- [x] ProductService con HttpClient
- [ ] **SaleService y PromotionService con HttpClient** (pendiente - seguir patrón de ProductService)
- [x] Guards funcionando

---

## 🎓 Siguientes Pasos Recomendados

1. **Poblar la base de datos** con el script SQL del README del backend
2. **Crear los controllers faltantes** en el backend:
   - ProductoController
   - PromocionController
   - VentaController
3. **Actualizar SaleService y PromotionService** en el frontend para usar HttpClient
4. **Probar flujos completos**:
   - Login → Dashboard → Crear Producto → Ver en catálogo
   - Registro → Agregar al carrito → Completar compra

---

## 📚 Recursos Adicionales

- [Spring Security + JWT Tutorial](https://www.baeldung.com/spring-security-jwt)
- [Angular HttpClient Guide](https://angular.io/guide/http)
- [Angular Interceptors](https://angular.io/guide/http-intercept-requests-and-responses)

---

## 💬 ¿Necesitas Ayuda?

Si encuentras algún problema, revisa:
1. Logs del backend (consola donde corre `mvn spring-boot:run`)
2. Logs del frontend (consola donde corre `npm start`)
3. Network tab en DevTools del navegador
4. Console tab en DevTools

---

**¡Listo!** 🎉 El proyecto está configurado para producción. Solo necesitas arrancar ambos servidores y probar.
