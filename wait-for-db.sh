#!/bin/sh
# wait-for-db.sh - espera a que MySQL acepte conexiones antes de arrancar la app
# Uso: COPY y ejecutar desde el contenedor o montar y usar como entrypoint wrapper

DB_HOST=${DB_HOST:-db}
DB_PORT=${DB_PORT:-3306}
RETRIES=${RETRIES:-30}
SLEEP_SEC=${SLEEP_SEC:-2}

i=0
echo "Waiting for MySQL at ${DB_HOST}:${DB_PORT} ..."
while ! nc -z ${DB_HOST} ${DB_PORT}; do
  i=$((i+1))
  if [ ${i} -ge ${RETRIES} ]; then
    echo "Timed out waiting for MySQL after ${RETRIES} attempts"
    exit 1
  fi
  sleep ${SLEEP_SEC}
done

echo "MySQL is reachable at ${DB_HOST}:${DB_PORT}"
exec "$@"