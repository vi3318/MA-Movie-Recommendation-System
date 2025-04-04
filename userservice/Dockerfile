FROM openjdk:23-jdk-slim

WORKDIR /app

COPY ./target/userservice-*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"] 
