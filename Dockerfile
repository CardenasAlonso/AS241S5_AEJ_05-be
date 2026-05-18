# Etapa 1: Construcción (Build con Caché de Dependencias)
FROM maven:3.9.6-amazoncorretto-17-alpine AS builder
WORKDIR /app

# 1.1 Copiar solo el POM para descargar dependencias primero y guardarlas en la caché de capas de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 1.2 Copiar el código fuente y empaquetar de forma nativa sin repetir descargas
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Entorno de Ejecución (Runtime con Imagen Oficial Certificada)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Añadimos un usuario sin privilegios por seguridad en contenedores
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiamos el archivo JAR compilado de la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Puerto expuesto documentado
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
