FROM openjdk:23-jdk-slim

WORKDIR /app

COPY ./target/moviecatalogservice-*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"] 
