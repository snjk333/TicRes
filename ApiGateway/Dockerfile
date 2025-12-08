# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy parent pom and ApiGateway pom
COPY pom.xml ./
COPY ApiGateway/pom.xml ./ApiGateway/

# Copy ApiGateway source code
COPY ApiGateway/src ./ApiGateway/src

# Build the application (skip other modules)
RUN mvn -f ApiGateway/pom.xml clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/ApiGateway/target/*.jar app.jar

# Expose port
EXPOSE 8000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8000/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
