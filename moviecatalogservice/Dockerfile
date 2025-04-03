# Use the official OpenJDK 23 image as the base image
FROM openjdk:23-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file from the target directory (Ensure you build the JAR using Maven)
COPY target/moviecatalogservice-0.0.1-SNAPSHOT.jar moviecatalogservice.jar

# Expose the application port
EXPOSE 8082

# Set environment variables
ENV SPRING_APPLICATION_NAME=moviecatalogservice
ENV SPRING_DATA_MONGODB_URI=mongodb+srv://harshkkamdar:harshkkamdar@alokbackend.zh783fq.mongodb.net/MovieReco
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/

# Run the application
ENTRYPOINT ["java", "-jar", "moviecatalogservice.jar"]
