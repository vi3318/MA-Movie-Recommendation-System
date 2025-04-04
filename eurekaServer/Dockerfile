FROM openjdk:23-jdk-slim

WORKDIR /app

COPY ./target/eurekaServer-*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"] 