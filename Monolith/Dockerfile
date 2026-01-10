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

# Install dependencies for entrypoint script
RUN apk update && apk add --no-cache curl jq

# Copy the jar from build stage
COPY --from=build /build/Monolith/target/*.jar app.jar

# Copy entrypoint script
COPY Monolith/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

# Expose port
EXPOSE 8080

# Run the application with entrypoint
ENTRYPOINT ["/app/entrypoint.sh"]
