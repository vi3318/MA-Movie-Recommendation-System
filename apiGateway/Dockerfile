# Use OpenJDK 23 slim image
FROM openjdk:23-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file to the container
COPY target/MAAPIGateway-0.0.1-SNAPSHOT.jar /app/api-gateway.jar

# Expose the port the API Gateway runs on
EXPOSE 8080

# Define the entry point for the container
ENTRYPOINT ["java", "-jar", "/app/api-gateway.jar"]
