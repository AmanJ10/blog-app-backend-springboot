FROM ubuntu:latest
LABEL authors="amanjoharapurkar"

ENTRYPOINT ["top", "-b"]

# Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app
COPY . .

# Build using Maven wrapper (skipping tests)
RUN ./mvnw clean package -DskipTests

# Stage 2: Run the application
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=builder /app/target/blog-0.0.1-SNAPSHOT.jar app.jar

# Expose the app port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

