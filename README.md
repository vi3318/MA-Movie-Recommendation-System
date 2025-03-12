# Movie Recommendation System

A microservices-based movie recommendation platform built with Spring Boot, MongoDB, OpenFeign, and Spring Cloud Netflix.

## Architecture

The application is composed of six microservices:

1. **User Service** - Manages user profiles, authentication, and watch history
2. **Movie Catalog Service** - Stores and manages movie information
3. **Review Service** - Handles movie reviews and ratings
4. **Recommendation Service** - Generates personalized movie recommendations
5. **Eureka Server** - Service registry for service discovery
6. **API Gateway** - Single entry point for all client requests, routing to appropriate services

## Prerequisites

- Java 17 or higher
- Maven
- MongoDB Atlas account (or local MongoDB instance)

## Configuration

All services are configured to connect to a single MongoDB Atlas database called "MovieReco". Each service uses its own collection within this database:

- User Service: Uses the "users" collection
- Movie Catalog Service: Uses the "movies" collection
- Review Service: Uses the "reviews" collection

The connection string is already set up in the application.properties files.

All microservices are configured to register with the Eureka Server for service discovery, and the API Gateway routes requests to the appropriate services.

## Running the Application

### 1. Clone the repository

```bash
git clone https://github.com/harshkkamdar/MA-Movie-Recommendation-System.git
cd MA-Movie-Recommendation-System
```

### 2. Build the services

```bash
# Build Eureka Server
cd eurekaServer
mvn clean package
cd ..

# Build API Gateway
cd apiGateway
mvn clean package
cd ..

# Build User Service
cd userservice
mvn clean package
cd ..

# Build Movie Catalog Service
cd moviecatalogservice
mvn clean package
cd ..

# Build Review Service
cd reviewservice
mvn clean package
cd ..

# Build Recommendation Service
cd recommendationservice
mvn clean package
cd ..
```

### 3. Run the services

Start each service in a separate terminal, making sure to start the Eureka Server first:

```bash
# Run Eureka Server (Port 8761)
cd eurekaServer
java -jar target/eurekaServer-0.0.1-SNAPSHOT.jar

# Run API Gateway (Port 8080)
cd apiGateway
java -jar target/apiGateway-0.0.1-SNAPSHOT.jar

# Run User Service (Port 8081)
cd userservice
java -jar target/userservice-0.0.1-SNAPSHOT.jar

# Run Movie Catalog Service (Port 8082)
cd moviecatalogservice
java -jar target/moviecatalogservice-0.0.1-SNAPSHOT.jar

# Run Review Service (Port 8083)
cd reviewservice
java -jar target/reviewservice-0.0.1-SNAPSHOT.jar

# Run Recommendation Service (Port 8084)
cd recommendationservice
java -jar target/recommendationservice-0.0.1-SNAPSHOT.jar
```

## API Documentation

Swagger UI is available for each service at:

- User Service: http://localhost:8081/swagger-ui.html
- Movie Catalog Service: http://localhost:8082/swagger-ui.html
- Review Service: http://localhost:8083/swagger-ui.html
- Recommendation Service: http://localhost:8084/swagger-ui.html

You can also access all services through the API Gateway:

- User Service: http://localhost:8080/api/users/**
- Movie Catalog Service: http://localhost:8080/api/movies/**
- Review Service: http://localhost:8080/api/reviews/**
- Recommendation Service: http://localhost:8080/api/recommendations/**

The Eureka Server dashboard is available at:
- http://localhost:8761

## Key Features

- **User Management:** Registration, login, profile management, and watch history
- **Movie Catalog:** Store and manage movie information
- **Reviews and Ratings:** Submit and retrieve movie reviews and ratings
- **Recommendation Engine:** Rule-based recommendations based on user preferences, watch history, and review ratings
- **Service Discovery:** Automatic registration and discovery of services using Eureka Server
- **API Gateway:** Centralized routing and load balancing for all client requests

## Security

The User Service implements JWT-based authentication. To access protected endpoints, you need to:

1. Register a user using `/api/auth/register`
2. Login using `/api/auth/login` to get a JWT token
3. Include the token in the Authorization header for subsequent requests: `Authorization: Bearer <token>`

## Inter-Service Communication

The microservices communicate with each other using OpenFeign clients and service discovery through Eureka. Here's how the communication flows:

### Recommendation Service
- Communicates with User Service to get user profiles, preferred genres, and watch history
- Communicates with Movie Catalog Service to get movie details and search for movies by genre
- Communicates with Review Service to get reviews and ratings for movies

### Review Service
- Communicates with Movie Catalog Service to validate that a movie exists before adding a review
- Communicates with User Service to validate that a user exists before adding a review

This ensures data integrity by preventing:
- Reviews for non-existent movies
- Reviews from non-existent users

### Service Discovery
All microservices register with the Eureka Server, allowing them to discover and communicate with each other without hardcoded URLs. This provides:
- Dynamic service discovery
- Load balancing
- Fault tolerance

### API Gateway
The API Gateway serves as a single entry point for all client requests, providing:
- Centralized routing to appropriate services
- Load balancing
- Potential for cross-cutting concerns like authentication, logging, etc.

### Testing Inter-Service Communication

You can test the inter-service communication using the following endpoints:

1. For Recommendation Service:
```
GET http://localhost:8084/api/recommendations/test-communication/{userId}
```
Or through the API Gateway:
```
GET http://localhost:8080/api/recommendations/test-communication/{userId}
```

2. For Review Service (adding a review will automatically validate movie and user existence):
```
POST http://localhost:8083/api/reviews
```
Or through the API Gateway:
```
POST http://localhost:8080/api/reviews
```
With a JSON body like:
```json
{
  "movieId": "existing-movie-id",
  "userId": "existing-user-id",
  "rating": 5,
  "comment": "Great movie!"
}
```

### Authentication for Inter-Service Communication

Both the Recommendation Service and Review Service include a `FeignClientConfig` that adds JWT authentication headers to requests made to other services. For testing purposes, you'll need to:

1. Login to get a JWT token using the User Service
2. Update the `FeignClientConfig` class in both services with your token:

```java
String bearerToken = "YOUR_JWT_TOKEN"; // Replace with your actual token
```

In a production environment, you would implement a more sophisticated token management strategy. 
