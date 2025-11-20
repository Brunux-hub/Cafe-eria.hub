# Etapa 1: Build de Angular
FROM node:20-alpine AS build

WORKDIR /app

# Copiar package.json y package-lock.json
COPY package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar el código fuente
COPY . .

# Build de producción
RUN npm run build -- --configuration production

# Etapa 2: Servidor Nginx
FROM nginx:alpine

# Copiar los archivos compilados al directorio de Nginx
COPY --from=build /app/dist/cafeteria-soma/browser /usr/share/nginx/html

# Copiar configuración personalizada de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto 80
EXPOSE 80

# Comando por defecto de Nginx
CMD ["nginx", "-g", "daemon off;"]
