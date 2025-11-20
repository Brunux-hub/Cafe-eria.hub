# 🚀 Deploy en Render

Guía completa para desplegar Cafetería Soma en [Render.com](https://render.com)

## 📋 Requisitos Previos

1. Cuenta en [Render.com](https://render.com) (gratis)
2. Repositorio en GitHub/GitLab con el código
3. Docker configurado localmente (opcional, para pruebas)

## 🎯 Opción 1: Deploy con Render Blueprint (Recomendado)

### Paso 1: Conectar Repositorio

1. Ve a [Render Dashboard](https://dashboard.render.com)
2. Click en **"New" → "Blueprint"**
3. Conecta tu repositorio de GitHub/GitLab
4. Render detectará automáticamente el archivo `render.yaml`

### Paso 2: Configurar Variables de Entorno

Render generará automáticamente:
- `MYSQL_ROOT_PASSWORD`
- `JWT_SECRET`

### Paso 3: Deploy Automático

Click en **"Apply"** y Render desplegará:
1. MySQL en un Private Service
2. Backend Spring Boot en Web Service
3. Frontend Angular en Web Service

**Tiempo estimado**: 10-15 minutos

### URLs Resultantes

- Frontend: `https://cafeteria-frontend.onrender.com`
- Backend API: `https://cafeteria-backend.onrender.com/api`

---

## 🎯 Opción 2: Deploy Manual Paso a Paso

### 1️⃣ Desplegar Base de Datos

**Opción A: MySQL en Render (Private Service)**

1. **New** → **Private Service**
2. Configuración:
   - **Name**: `cafeteria-mysql`
   - **Environment**: Docker
   - **Dockerfile Path**: `./docker/mysql.Dockerfile`
   - **Disk**: 10GB en `/var/lib/mysql`
3. Variables de entorno:
   ```
   MYSQL_ROOT_PASSWORD=<genera-uno-seguro>
   MYSQL_DATABASE=cafeteria_soma
   ```

**Opción B: MySQL Externo (Aiven, PlanetScale, Railway)**

Si prefieres usar un servicio externo de MySQL, obtén la connection string.

### 2️⃣ Desplegar Backend

1. **New** → **Web Service**
2. Conecta tu repositorio
3. Configuración:
   - **Name**: `cafeteria-backend`
   - **Region**: Elige el más cercano
   - **Branch**: `main`
   - **Root Directory**: `cafeteria-soma-backend`
   - **Environment**: Docker
   - **Dockerfile Path**: `./Dockerfile`
   - **Port**: 8080

4. **Variables de entorno**:
   ```
   SPRING_DATASOURCE_URL=jdbc:mysql://<MYSQL_HOST>:3306/cafeteria_soma?useSSL=true&serverTimezone=UTC
   SPRING_DATASOURCE_USERNAME=root
   SPRING_DATASOURCE_PASSWORD=<tu-password-mysql>
   JWT_SECRET=<genera-uno-con-256-bits>
   JWT_EXPIRATION=86400000
   SPRING_PROFILES_ACTIVE=prod
   ```

5. **Health Check Path**: `/actuator/health`

6. Click **Create Web Service**

### 3️⃣ Desplegar Frontend

1. **New** → **Web Service**
2. Conecta tu repositorio
3. Configuración:
   - **Name**: `cafeteria-frontend`
   - **Region**: Mismo que el backend
   - **Branch**: `main`
   - **Root Directory**: `cafeteria-soma`
   - **Environment**: Docker
   - **Dockerfile Path**: `./Dockerfile`
   - **Port**: 80

4. **Variables de entorno**:
   ```
   API_URL=https://cafeteria-backend.onrender.com
   ```

5. Click **Create Web Service**

---

## 🔧 Configuraciones Adicionales para Producción

### Backend - application-prod.properties

Crea este archivo en `cafeteria-soma-backend/src/main/resources/`:

```properties
# Configuración de producción
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

# JPA para producción
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=false

# Actuator solo para health
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=never

# Seguridad JWT
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}

# CORS para frontend de Render
allowed.origins=https://cafeteria-frontend.onrender.com

# Logs
logging.level.root=WARN
logging.level.com.cafeteriasoma=INFO
```

### Frontend - Actualizar environment.prod.ts

```typescript
export const environment = {
  production: true,
  apiBaseUrl: 'https://cafeteria-backend.onrender.com/api'
};
```

### Backend - Actualizar SecurityConfig.java

```java
@Value("${allowed.origins:http://localhost:4200}")
private String allowedOrigins;

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

## 📊 Plan de Render Recomendado

### Free Tier (Gratis)
- ✅ Backend y Frontend
- ⚠️ Se duerme después de 15 min de inactividad
- ⚠️ 750 horas/mes de servicio
- ❌ No incluye MySQL privado

### Starter ($7/mes por servicio)
- ✅ Siempre activo
- ✅ Sin límite de horas
- ✅ SSL automático
- ✅ Puedes agregar MySQL como Private Service

### Recomendación para Producción
- **Backend**: Starter ($7/mes)
- **Frontend**: Free (suficiente)
- **MySQL**: External (Aiven Free o PlanetScale Hobby)

**Total**: ~$7/mes

---

## 🗄️ Alternativas para Base de Datos

### 1. Aiven MySQL (Free Tier)
- 1GB RAM, 5GB storage
- URL: https://aiven.io

### 2. PlanetScale (Hobby Plan)
- 5GB storage, 1 billion row reads/mes
- Compatible con MySQL
- URL: https://planetscale.com

### 3. Railway MySQL ($5/mes)
- 512MB RAM, 1GB storage
- URL: https://railway.app

### 4. Render Private Service ($7/mes)
- MySQL 8.0 dedicado
- 1GB RAM

---

## 🔐 Generación de Secrets Seguros

### JWT Secret (256 bits)

```bash
# En terminal (Git Bash o Linux)
openssl rand -base64 32

# En PowerShell
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))

# Online (si no tienes openssl)
# https://generate-secret.vercel.app/32
```

### MySQL Root Password

```bash
openssl rand -base64 24
```

---

## 🚀 Deploy Automático con GitHub

### 1. Habilitar Auto-Deploy

En cada servicio de Render:
1. **Settings** → **Build & Deploy**
2. Activa **"Auto-Deploy"**
3. Elige la rama `main`

### 2. Cada push a `main` desplegará automáticamente

```bash
git add .
git commit -m "Deploy to Render"
git push origin main
```

Render detectará el push y redesplegará automáticamente.

---

## 🐛 Troubleshooting

### Backend no inicia

**Logs a revisar**:
```bash
# En Render Dashboard → Backend Service → Logs
```

**Problemas comunes**:
1. **Error de conexión a MySQL**
   - Verifica `SPRING_DATASOURCE_URL`
   - Asegúrate que MySQL esté corriendo
   - Revisa el host (usa el internal hostname de Render)

2. **JWT Secret inválido**
   - Genera uno nuevo con al menos 256 bits
   - Usa solo caracteres base64

### Frontend no carga

**Verificar**:
1. Backend esté respondiendo en `/actuator/health`
2. `API_URL` en variables de entorno apunte al backend correcto
3. CORS esté configurado correctamente en el backend

### MySQL se reinicia constantemente

**Posibles causas**:
1. Memoria insuficiente (upgrade a plan Starter)
2. Disco lleno (aumenta storage)
3. Healthcheck fallando (ajusta timeout)

---

## 📈 Monitoreo

### Render Dashboard
- CPU, RAM, Network usage
- Logs en tiempo real
- Métricas de requests

### Endpoints de Health

```bash
# Backend health
curl https://cafeteria-backend.onrender.com/actuator/health

# Frontend
curl https://cafeteria-frontend.onrender.com
```

---

## 💰 Costos Estimados

### Opción Económica (Free + External DB)
- Frontend: Free
- Backend: Free (con sleep)
- MySQL: Aiven Free
- **Total: $0/mes** ⚠️ Con limitaciones

### Opción Producción (Siempre Activo)
- Frontend: Free
- Backend: Starter $7/mes
- MySQL: PlanetScale Hobby Free o Aiven $10/mes
- **Total: $7-17/mes** ✅ Recomendado

### Opción Premium (Todo en Render)
- Frontend: Free
- Backend: Starter $7/mes
- MySQL: Private Service $7/mes
- **Total: $14/mes** 🚀 Mejor performance

---

## 📝 Checklist de Deploy

- [ ] Código en GitHub/GitLab
- [ ] `render.yaml` configurado (para Blueprint)
- [ ] `Dockerfile` en backend y frontend
- [ ] `application-prod.properties` creado
- [ ] `environment.prod.ts` actualizado
- [ ] Generar JWT_SECRET seguro
- [ ] Crear servicios en Render:
  - [ ] MySQL (o externo)
  - [ ] Backend
  - [ ] Frontend
- [ ] Configurar variables de entorno
- [ ] Configurar CORS con URL de producción
- [ ] Ejecutar `init-data.sql` en MySQL producción
- [ ] Probar login con usuario admin
- [ ] Verificar productos en catálogo
- [ ] Habilitar auto-deploy

---

## 🎉 URLs Finales

Después del deploy exitoso:

- **Aplicación**: https://cafeteria-frontend.onrender.com
- **API Docs**: https://cafeteria-backend.onrender.com/actuator/health
- **Admin Login**: admin@cafeteriasoma.com / admin123

---

## 🔄 Actualizaciones

Para actualizar la aplicación:

```bash
# 1. Hacer cambios locales
git add .
git commit -m "Update: nueva funcionalidad"

# 2. Push a GitHub
git push origin main

# 3. Render auto-despliega (si auto-deploy está activo)
# O manualmente: Render Dashboard → Service → Manual Deploy
```

---

## 📚 Recursos

- [Render Docs](https://render.com/docs)
- [Render Blueprint Spec](https://render.com/docs/blueprint-spec)
- [Deploy Spring Boot](https://render.com/docs/deploy-spring-boot)
- [Deploy Docker](https://render.com/docs/docker)

¿Necesitas ayuda con algún paso específico? 🚀
