FROM openjdk:23-jdk-slim

WORKDIR /app

COPY ./target/recommendationservice-*.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"] 