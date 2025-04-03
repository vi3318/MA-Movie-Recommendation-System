# Use the official OpenJDK 23 image as the base image
FROM openjdk:23-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the target directory (ensure you build the JAR using Maven)
COPY target/recommendationservice-0.0.1-SNAPSHOT.jar recommendationservice.jar

# Expose the port that the service runs on
EXPOSE 8084

# Run the application
ENTRYPOINT ["java", "-jar", "recommendationservice.jar"]
