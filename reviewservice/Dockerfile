# Use OpenJDK 23 slim image
FROM openjdk:23-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file to the container
COPY target/reviewservice-0.0.1-SNAPSHOT.jar /app/reviewservice.jar

# Expose the port the review service runs on
EXPOSE 8083

# Define the entry point for the container
ENTRYPOINT ["java", "-jar", "/app/reviewservice.jar"]
