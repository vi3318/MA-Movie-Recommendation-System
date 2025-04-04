FROM openjdk:23-jdk-slim

WORKDIR /app

COPY ./target/reviewservice-*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"] 