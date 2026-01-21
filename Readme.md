# jobapp-backend - Docker (multi-module) + depuración

## Requisitos
- Docker Desktop (Linux containers) en Windows
- JDK 17 y Maven (opcional si usas mvnw)
- VS Code con extensiones Java y Debugger for Java

## Construir imagen de producción
docker build -t jobapp-backend:prod -f Dockerfile .

## Ejecutar imagen de producción
docker run --rm -p 8080:8080 --name jobapp-prod jobapp-backend:prod

## Construir imagen de depuración (con JDWP)
docker build -t jobapp-backend:debug -f Dockerfile.debug .

## Ejecutar imagen de depuración (mapear puerto 5005)
docker run --rm -p 8080:8080 -p 5005:5005 --name jobapp-dev jobapp-backend:debug

## Forzar que la JVM espere al depurador
# Edita Dockerfile.debug: cambiar suspend=n por suspend=y, rebuild y run.
# Con suspend=y la JVM no ejecuta la app hasta que VS Code se conecte.

## Iteración rápida sin rebuild (opción)
# Compilar localmente y montar target dentro del contenedor
mvn -DskipTests package
docker run --rm -p 8080:8080 -p 5005:5005 -v ${PWD}/jobapp-web/target:/app/target --name jobapp-dev jobapp-backend:debug

## Depurar desde VS Code
1. Inicia el contenedor (ver arriba).
2. En VS Code, Run and Debug → seleccionar "Attach to Docker JDWP".
3. Coloca breakpoints y comienza la depuración.

## Comandos útiles
docker ps
docker logs -f jobapp-dev
docker exec -it jobapp-dev sh