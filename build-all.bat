@echo off
echo Building all services...

echo Building Eureka Server...
cd eurekaServer
call mvnw clean package -DskipTests
docker build -t eureka-server .
cd ..

echo Building API Gateway...
cd apiGateway
call mvnw clean package -DskipTests
docker build -t api-gateway .
cd ..

echo Building User Service...
cd userservice
call mvnw clean package -DskipTests
docker build -t user-service .
cd ..

echo Building Movie Catalog Service...
cd moviecatalogservice
call mvnw clean package -DskipTests
docker build -t movie-catalog-service .
cd ..

echo Building Review Service...
cd reviewservice
call mvnw clean package -DskipTests
docker build -t review-service .
cd ..

echo Building Recommendation Service...
cd recommendationservice
call mvnw clean package -DskipTests
docker build -t recommendation-service .
cd ..

echo All services built successfully!
echo You can now run the services using Docker Compose or deploy them to Kubernetes. 