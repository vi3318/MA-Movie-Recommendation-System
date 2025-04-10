# Movie Recommendation System - Microservices Architecture

This document provides a technical overview of the microservices that compose the Movie Recommendation System.

## 1. Eureka Server

**Purpose**: Service discovery and registration server.

**Technical Implementation**:
- Built using Spring Cloud Netflix Eureka Server
- Enables service registration, discovery, and load balancing
- Acts as the central registry for all microservices

**Key Components**:
- `@EnableEurekaServer` annotation to activate Eureka Server functionality
- Runs on default port 8761
- Maintains a registry of service instances with health information

## 2. API Gateway

**Purpose**: Entry point for all client requests, routing traffic to appropriate services.

**Technical Implementation**:
- Built using Spring Cloud Gateway
- Registers itself with Eureka Server
- Dynamically routes requests to registered services

**Key Features**:
- Route configuration based on service ID and path patterns
- Load balancing using service discovery
- Path-based routing to microservices with pattern matching
- Health checking and monitoring via Spring Boot Actuator

**Routes Configuration**:
- `/api/users/**` → User Service 
- `/api/movies/**` → Movie Catalog Service
- `/api/reviews/**` → Review Service
- `/api/recommendations/**` → Recommendation Service
- `/api/auth/**` → User Service (Auth endpoints)

## 3. Movie Catalog Service

**Purpose**: Manages the catalog of movies with their metadata.

**Technical Implementation**:
- Spring Boot application with MongoDB for data storage
- Provides REST API for movie operations
- Registers with Eureka for service discovery

**Key Endpoints**:
- `GET /api/movies` - Get all movies
- `GET /api/movies/{id}` - Get movie by ID
- `POST /api/movies` - Add a new movie
- `PUT /api/movies/{id}` - Update movie information
- `DELETE /api/movies/{id}` - Delete a movie
- `GET /api/movies/search` - Search movies by various criteria (title, genres, releaseYear, director, actors)

**Data Model**:
- Movie entity with fields for title, description, genres, director, actors, release year, etc.

## 4. Review Service

**Purpose**: Handles movie reviews and ratings submitted by users.

**Technical Implementation**:
- Spring Boot service with MongoDB for storing reviews
- Communicates with other services via OpenFeign
- Registers with Eureka for service discovery

**Key Endpoints**:
- `GET /api/reviews` - Get all reviews
- `GET /api/reviews/{id}` - Get review by ID
- `GET /api/reviews/movie/{movieId}` - Get reviews for a specific movie
- `GET /api/reviews/user/{userId}` - Get reviews by a specific user
- `GET /api/reviews/rating/{minRating}` - Get reviews with rating above threshold
- `GET /api/reviews/movie/{movieId}/average-rating` - Get average rating for a movie
- `POST /api/reviews` - Add a new review
- `PUT /api/reviews/{id}` - Update a review
- `DELETE /api/reviews/{id}` - Delete a review

**Data Model**:
- Review entity with userId, movieId, rating, comment, and timestamp

## 5. User Service

**Purpose**: Manages user accounts, authentication, and user preferences.

**Technical Implementation**:
- Spring Boot service with MongoDB for user data storage
- Implements Spring Security with JWT for authentication
- Registers with Eureka for service discovery

**Key Components**:
- Authentication Controller - handles login, signup, token validation
- User Controller - manages user profiles and preferences
- JWT Security Configuration - secure endpoints and token-based authentication

**Features**:
- User registration and authentication
- Password encryption with Spring Security
- JWT token generation and validation
- User profile management
- User preference tracking for recommendation algorithm

## 6. Recommendation Service

**Purpose**: Generates personalized movie recommendations for users.

**Technical Implementation**:
- Spring Boot service using OpenFeign for inter-service communication
- Implements multiple recommendation algorithms
- Aggregates data from other services to generate recommendations

**Key Endpoints**:
- `GET /api/recommendations/users/{userId}` - Get general recommendations for user
- `GET /api/recommendations/users/{userId}/genre` - Get recommendations based on user's genre preferences
- `GET /api/recommendations/users/{userId}/rating` - Get recommendations based on highly-rated movies
- `GET /api/recommendations/users/{userId}/similar-users` - Get recommendations based on similar users
- `GET /api/recommendations/users/{userId}/top10` - Get top 10 recommendations
- `GET /api/recommendations/users/{userId}/detailed` - Get detailed recommendations with explanations

**Recommendation Methods**:
- Genre-based recommendations - suggests movies from user's preferred genres
- Rating-based recommendations - suggests highly-rated movies
- Collaborative filtering - suggests movies liked by similar users
- Combined approach - weighted combination of multiple algorithms

## Inter-Service Communication

The microservices communicate with each other through:
1. **Service Discovery** - Eureka Server enables services to find each other
2. **OpenFeign Clients** - Declarative REST clients for service-to-service calls
3. **API Gateway** - Central routing for external requests

## Deployment and Containerization

### Docker Implementation

Each microservice is containerized using Docker with its own Dockerfile. The Dockerfiles follow a consistent pattern:

```
FROM openjdk:23-jdk-slim
WORKDIR /app
COPY ./target/<service-name>-*.jar app.jar
EXPOSE <service-port>
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Key Docker Features**:
- Lightweight JDK images for smaller container size
- Consistent application packaging across all services
- Isolated runtime environments for each service
- Exposed ports for service communication

### Docker Compose

The system leverages Docker Compose for local development and testing, defined in `docker-compose.yml`:

**Structure**:
- Defines all 6 microservices as containers
- Configures a dedicated network (`movie-network`) for inter-service communication
- Maps container ports to host ports for external access
- Sets dependencies between services (e.g., all services depend on Eureka Server)
- Configures environment variables for service discovery

**Service Configuration Example**:
```yaml
api-gateway:
  build: ./apiGateway
  ports:
    - "8080:8080"
  depends_on:
    - eureka-server
  environment:
    - EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-server:8761/eureka/
  networks:
    - movie-network
```

### Kubernetes Deployment

For production deployment, the system uses Kubernetes manifests in the `k8s` directory:

**Common Components in Each Service Manifest**:
1. **Deployment**:
   - Container image configuration (`image: vdharia18/<service-name>:latest`)
   - Resource limits and requests for CPU and memory
   - Health checks (readiness and liveness probes)
   - Environment variables for service configuration
   - Replica count for horizontal scaling

2. **Service**:
   - ClusterIP type for internal communication
   - Port mappings for service discovery
   - Selector labels for pod targeting

**Kubernetes Features Used**:
- Health probes for automatic container restart on failure
- Resource constraints to ensure optimal cluster utilization
- Service discovery through Kubernetes DNS
- Environment variable injection for configuration
- Horizontal scaling capability

**Example (API Gateway)**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: api-gateway
spec:
  replicas: 1
  selector:
    matchLabels:
      app: api-gateway
  template:
    metadata:
      labels:
        app: api-gateway
    spec:
      containers:
        - name: api-gateway
          image: vdharia18/ma-api-gateway:latest
          # Configuration, env vars, probes, etc.
---
apiVersion: v1
kind: Service
metadata:
  name: api-gateway
spec:
  selector:
    app: api-gateway
  ports:
    - port: 8080
      targetPort: 8080
  type: ClusterIP
```

This multi-layered deployment approach provides flexibility for different environments:
- **Development**: Local Docker Compose setup
- **Testing**: Kubernetes deployment in test environment
- **Production**: Scalable Kubernetes deployment with proper resource allocation 