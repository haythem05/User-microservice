# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# Copy only the files needed to resolve dependencies first
COPY ./pom.xml ./
COPY ./src ./src

# Build the JAR without running tests
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17-alpine
WORKDIR /app

# Copy built JAR from the previous stage
COPY --from=build /app/target/*.jar ./app.jar

# Expose port
EXPOSE 8081

# Start Spring Boot app, allow profile to be passed via environment
CMD ["java", "-jar", "app.jar"]
