FROM mysql:8.0

# Variables de entorno
ENV MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD}
ENV MYSQL_DATABASE=cafeteria_soma

# Copiar script de inicialización
COPY ../cafeteria-soma-backend/init-data.sql /docker-entrypoint-initdb.d/

# Exponer puerto
EXPOSE 3306

# Configuración de MySQL para Render
RUN echo "[mysqld]" >> /etc/mysql/conf.d/custom.cnf && \
    echo "max_connections=50" >> /etc/mysql/conf.d/custom.cnf && \
    echo "max_allowed_packet=64M" >> /etc/mysql/conf.d/custom.cnf

# Healthcheck
HEALTHCHECK --interval=10s --timeout=5s --retries=5 \
  CMD mysqladmin ping -h localhost -u root -p${MYSQL_ROOT_PASSWORD}
