FROM ubuntu:latest
LABEL authors="amanjoharapurkar"

ENTRYPOINT ["top", "-b"]

# Use official Java runtime
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Build the app
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/*.jar"]
