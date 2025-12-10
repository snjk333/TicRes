# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copy entire project
COPY . .

# Build everything
RUN mvn clean install -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the jar from build stage
COPY --from=build /build/notificationService/target/*.jar app.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
