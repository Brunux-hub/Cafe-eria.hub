# 🐳 Docker Setup - Cafetería Soma

Configuración completa con Docker Compose para ejecutar toda la aplicación en contenedores.

## 📦 Servicios Incluidos

- **MySQL 8.0**: Base de datos en puerto `3306`
- **Adminer**: Gestor de BD web en puerto `8081`
- **Backend Spring Boot**: API REST en puerto `8080`
- **Frontend Angular**: Aplicación web en puerto `80`

## 🚀 Inicio Rápido

### 1. Construir y levantar todos los servicios

```bash
docker-compose up --build
```

### 2. Solo levantar (si ya están construidos)

```bash
docker-compose up
```

### 3. Levantar en background

```bash
docker-compose up -d
```

## 🌐 Acceso a los Servicios

| Servicio | URL | Credenciales |
|----------|-----|--------------|
| **Frontend** | http://localhost | - |
| **Backend API** | http://localhost:8080/api | - |
| **Adminer** | http://localhost:8081 | Ver abajo ⬇️ |
| **MySQL** | localhost:3306 | Ver abajo ⬇️ |

### 🔑 Credenciales Adminer

- **Sistema**: MySQL
- **Servidor**: `mysql` (nombre del contenedor)
- **Usuario**: `root`
- **Contraseña**: `Antony03`
- **Base de datos**: `cafeteria_soma`

### 👤 Usuarios de Prueba (Frontend)

Después de que se inicialice la BD automáticamente:

- **Admin**: `admin@cafeteriasoma.com` / `admin123`
- **Cliente**: `juan@email.com` / `password123`
- **Cliente**: `maria@email.com` / `password123`

## 🛠️ Comandos Útiles

### Ver logs de todos los servicios

```bash
docker-compose logs -f
```

### Ver logs de un servicio específico

```bash
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f mysql
```

### Detener todos los servicios

```bash
docker-compose down
```

### Detener y eliminar volúmenes (⚠️ borra la BD)

```bash
docker-compose down -v
```

### Reconstruir un servicio específico

```bash
docker-compose up --build backend
docker-compose up --build frontend
```

### Ver estado de los contenedores

```bash
docker-compose ps
```

### Ejecutar comandos dentro de un contenedor

```bash
# Acceder al contenedor MySQL
docker-compose exec mysql bash

# Acceder a MySQL CLI
docker-compose exec mysql mysql -uroot -pAntony03 cafeteria_soma

# Ver logs del backend
docker-compose exec backend cat /app/logs/spring.log
```

## 🔄 Orden de Inicio

Los servicios se inician en el siguiente orden automáticamente:

1. **MySQL** (con healthcheck)
2. **Adminer** (espera a MySQL)
3. **Backend** (espera a MySQL healthy)
4. **Frontend** (espera a Backend)

## 📊 Healthchecks

- **MySQL**: Verifica cada 10s con `mysqladmin ping`
- **Backend**: Verifica cada 30s en `/actuator/health`

## 🗄️ Persistencia de Datos

Los datos de MySQL se almacenan en un volumen Docker llamado `mysql_data`, por lo que persisten incluso si detienes los contenedores.

Para resetear completamente la base de datos:

```bash
docker-compose down -v
docker-compose up --build
```

## 🐛 Troubleshooting

### El backend no inicia

```bash
# Ver logs completos
docker-compose logs backend

# Verificar que MySQL esté healthy
docker-compose ps mysql
```

### Error de conexión a la BD

```bash
# Verificar que MySQL acepte conexiones
docker-compose exec mysql mysqladmin ping -uroot -pAntony03

# Reiniciar solo MySQL
docker-compose restart mysql
```

### El frontend no carga

```bash
# Verificar logs de Nginx
docker-compose logs frontend

# Verificar que el backend esté respondiendo
curl http://localhost:8080/actuator/health
```

### Limpiar todo y empezar de cero

```bash
# Detener y eliminar contenedores, redes y volúmenes
docker-compose down -v

# Eliminar imágenes construidas
docker rmi cafeteria-soma-backend cafeteria-soma-frontend

# Reconstruir todo
docker-compose up --build
```

## 🔧 Configuración de Desarrollo

Si quieres hacer cambios y reconstruir:

### Backend

```bash
# Reconstruir solo el backend
docker-compose up --build backend

# O con down primero
docker-compose down
docker-compose up --build backend
```

### Frontend

```bash
# Reconstruir solo el frontend
docker-compose up --build frontend
```

## 📝 Variables de Entorno

Puedes modificar las variables en `docker-compose.yml`:

```yaml
environment:
  MYSQL_ROOT_PASSWORD: Antony03
  MYSQL_DATABASE: cafeteria_soma
  JWT_SECRET: tu-secreto-jwt
  JWT_EXPIRATION: 86400000
```

## 🚢 Producción

Para producción, considera:

1. Usar secrets en lugar de contraseñas en texto plano
2. Configurar HTTPS con certificados SSL
3. Usar variables de entorno externas
4. Configurar backups automáticos de MySQL
5. Ajustar recursos de memoria y CPU

Ejemplo con archivo `.env`:

```bash
# Crear archivo .env
MYSQL_ROOT_PASSWORD=tu-password-seguro
JWT_SECRET=tu-jwt-secret-seguro

# Referenciar en docker-compose.yml
environment:
  MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
```

## 📦 Arquitectura de Contenedores

```
┌─────────────────────────────────────────┐
│          Red: cafeteria-network         │
│                                         │
│  ┌──────────┐  ┌──────────┐            │
│  │ Frontend │  │ Adminer  │            │
│  │  :80     │  │  :8081   │            │
│  └────┬─────┘  └────┬─────┘            │
│       │             │                   │
│       ▼             ▼                   │
│  ┌──────────┐  ┌──────────┐            │
│  │ Backend  │  │  MySQL   │            │
│  │  :8080   │──│  :3306   │            │
│  └──────────┘  └──────────┘            │
│                     │                   │
│                     ▼                   │
│              [mysql_data volume]        │
└─────────────────────────────────────────┘
```

## ✅ Checklist de Inicio

- [ ] Docker y Docker Compose instalados
- [ ] Puertos 80, 8080, 8081, 3306 disponibles
- [ ] Ejecutar `docker-compose up --build`
- [ ] Esperar a que todos los servicios estén "healthy"
- [ ] Acceder a http://localhost
- [ ] Login con admin@cafeteriasoma.com / admin123
- [ ] Verificar productos en el catálogo
- [ ] Acceder a Adminer en http://localhost:8081

🎉 ¡Listo! Toda la aplicación corriendo en contenedores.
