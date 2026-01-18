# Stage 1: build the multi-module project
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy only Maven metadata first for better caching
COPY pom.xml .
COPY jobapp-service/pom.xml jobapp-service/
COPY jobapp-facade/pom.xml jobapp-facade/
COPY jobapp-web/pom.xml jobapp-web/

# Download dependencies
RUN mvn -B -f pom.xml -T 1C dependency:go-offline

# Copy full sources
COPY . .

# Build the whole reactor (skip tests for speed)
RUN mvn -B -f pom.xml -T 1C -DskipTests package

# Stage 2: runtime
FROM eclipse-temurin:21-jre-alpine
ARG JAR_FILE=/workspace/jobapp-web/target/*-SNAPSHOT.jar
# Fallback: try any jar in target if SNAPSHOT name differs
COPY --from=build ${JAR_FILE} /app/app.jar

EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx512m -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]