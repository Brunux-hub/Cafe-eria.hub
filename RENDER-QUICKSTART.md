# 🚀 Quick Deploy en Render

## Opción 1: Blueprint (Más Rápido) ⚡

1. **Sube el código a GitHub**:
   ```bash
   git add .
   git commit -m "Deploy to Render"
   git push origin main
   ```

2. **Ve a Render**: https://dashboard.render.com

3. **New → Blueprint**

4. **Conecta tu repositorio** y Render detectará `render.yaml`

5. **Genera secretos**:
   ```powershell
   # JWT Secret (PowerShell)
   [Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
   ```

6. **Click "Apply"** y espera 10-15 minutos

7. **Ejecuta init-data.sql** en la BD:
   - Entra a MySQL desde Render Shell
   - Copia y pega el contenido de `init-data.sql`

8. **¡Listo!** Accede a tu app en:
   - https://cafeteria-frontend.onrender.com

---

## Opción 2: Manual (Paso a Paso) 🔧

### 1. Base de Datos MySQL

**Recomendado**: Usa un servicio externo para ahorrarte $7/mes

#### Opción A: Aiven (Free)
1. Regístrate en https://aiven.io
2. Crea MySQL 8.0 (Free tier)
3. Copia la connection string

#### Opción B: PlanetScale (Free)
1. Regístrate en https://planetscale.com
2. Crea nueva DB
3. Copia la connection string

#### Opción C: Render Private Service ($7/mes)
1. New → Private Service
2. Dockerfile: `./docker/mysql.Dockerfile`
3. Disk: 10GB en `/var/lib/mysql`

### 2. Backend Spring Boot

1. **New → Web Service**
2. **Conecta repositorio**
3. **Configuración**:
   - Root Directory: `cafeteria-soma-backend`
   - Environment: Docker
   - Dockerfile Path: `./Dockerfile`
   - Port: 8080

4. **Variables de entorno**:
   ```
   SPRING_DATASOURCE_URL=jdbc:mysql://<TU-HOST>:3306/cafeteria_soma
   SPRING_DATASOURCE_USERNAME=<TU-USER>
   SPRING_DATASOURCE_PASSWORD=<TU-PASSWORD>
   JWT_SECRET=<GENERA-CON-POWERSHELL>
   JWT_EXPIRATION=86400000
   ALLOWED_ORIGINS=https://cafeteria-frontend.onrender.com
   SPRING_PROFILES_ACTIVE=prod
   ```

5. **Health Check**: `/actuator/health`

6. **Create Web Service**

### 3. Frontend Angular

1. **New → Web Service**
2. **Configuración**:
   - Root Directory: `cafeteria-soma`
   - Environment: Docker
   - Dockerfile Path: `./Dockerfile`
   - Port: 80

3. **NO necesita variables de entorno** (ya está en environment.prod.ts)

4. **Create Web Service**

### 4. Inicializar Base de Datos

Una vez el backend esté corriendo:

1. Conecta a tu MySQL (Adminer, MySQL Workbench, etc.)
2. Ejecuta el script `cafeteria-soma-backend/init-data.sql`
3. Verifica que existan:
   - 2 roles
   - 3 usuarios
   - 6 categorías
   - 15 productos

### 5. Probar la Aplicación

1. Accede a: `https://cafeteria-frontend.onrender.com`
2. Login con: `admin@cafeteriasoma.com` / `admin123`
3. Navega por productos, carrito, etc.

---

## ⚙️ Generar JWT Secret

```powershell
# PowerShell (Windows)
[Convert]::ToBase64String([System.Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

```bash
# Bash (Linux/Mac)
openssl rand -base64 32
```

---

## 🔄 Actualizar después del deploy

```bash
git add .
git commit -m "Update feature"
git push origin main
```

Render auto-desplegará si tienes **Auto-Deploy** activado en Settings.

---

## 💰 Costos

### Opción Económica (Free)
- Frontend: Free ✅
- Backend: Free (se duerme) ⚠️
- MySQL: Aiven/PlanetScale Free ✅
- **Total: $0/mes**

### Opción Producción (Recomendado)
- Frontend: Free ✅
- Backend: Starter $7/mes ✅
- MySQL: Aiven/PlanetScale Free ✅
- **Total: $7/mes** 🎯

---

## 🐛 Troubleshooting

### Backend no inicia
```bash
# Ver logs
Render Dashboard → Backend Service → Logs

# Verificar variables de entorno
Settings → Environment
```

### Error de CORS
Verifica que `ALLOWED_ORIGINS` incluya tu frontend:
```
ALLOWED_ORIGINS=https://cafeteria-frontend.onrender.com,http://localhost:4200
```

### MySQL rechaza conexión
- Verifica host, user y password
- Asegúrate que MySQL acepte conexiones externas
- En Aiven/PlanetScale, usa la connection string exacta

---

## 📚 Documentación Completa

Lee `RENDER-DEPLOY.md` para detalles completos sobre:
- Configuraciones avanzadas
- Monitoreo y logs
- Backups de base de datos
- Optimizaciones de performance
- Estrategias de CI/CD

---

## ✅ Checklist

- [ ] Código en GitHub
- [ ] Crear cuenta en Render
- [ ] Crear MySQL (Aiven/PlanetScale/Render)
- [ ] Generar JWT_SECRET
- [ ] Desplegar Backend con variables correctas
- [ ] Desplegar Frontend
- [ ] Ejecutar init-data.sql
- [ ] Probar login admin
- [ ] Habilitar Auto-Deploy

🎉 ¡App desplegada en producción!
