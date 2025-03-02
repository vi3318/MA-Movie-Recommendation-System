Below is an updated, detailed Product Requirements Document (PRD) along with step-by-step instructions for setting up your microservices. This version uses only MongoDB as the database, employs OpenFeign for inter-service communication, and integrates Swagger for API documentation. The optional services (Eureka Server and API Gateway) have been removed from this scope.

---

## Product Requirements Document (PRD)

### 1. Project Overview

**Objective:**  
Develop a rule-based Movie Recommendation Platform using a microservices architecture with Spring Boot. The platform will allow users to register, manage profiles, view a movie catalog, submit reviews/ratings, and receive personalized movie recommendations. The system is built entirely on NoSQL (MongoDB) and uses OpenFeign for inter-service communication.

**Key Features:**
- **User Management:**  
  - User registration, login, profile management, and watch history.
  - Secure authentication using JWT integrated with Spring Security.
  
- **Movie Catalog:**  
  - Store and manage movie information (which may be fetched from an external API like TMDB if needed).
  - Provide endpoints to retrieve and search movie details.
  
- **Reviews and Ratings:**  
  - Allow users to submit reviews and ratings for movies.
  - Store review data in MongoDB.
  
- **Recommendation Engine:**  
  - Implement rule-based recommendations based on user preferences, watch history, and review ratings.
  - Utilize OpenFeign to gather required data from other services.
  
- **API Documentation:**  
  - Integrate Swagger in every microservice for comprehensive API documentation.

### 2. System Architecture

**Overview:**  
The application is decomposed into four core microservices. Each microservice is independently deployable and communicates with the others using OpenFeign for a clean, declarative REST client approach. All services use MongoDB as the primary data store.

**Microservices:**

1. **User Profile Service:**
   - **Responsibilities:**  
     - Handle user registration, authentication, profile management, and storing watch history.
   - **Technologies:**  
     - Spring Boot Web, Spring Data MongoDB, Spring Security, JWT, and OpenFeign.
   - **APIs:**  
     - Endpoints for user registration, login, profile updates, and fetching user history.
   - **Inter-service Communication:**  
     - Expose REST endpoints for other services to query user data via OpenFeign.

2. **Movie Catalog Service:**
   - **Responsibilities:**  
     - Manage movie data including storing details such as title, genre, and description.
     - Optionally integrate with external movie APIs (like TMDB) using WebClient.
   - **Technologies:**  
     - Spring Boot Web, Spring Data MongoDB, OpenFeign.
   - **APIs:**  
     - Endpoints to list movies, fetch movie details, and search movies.

3. **Review & Ratings Service:**
   - **Responsibilities:**  
     - Allow users to submit and fetch movie reviews and ratings.
   - **Technologies:**  
     - Spring Boot Web, Spring Data MongoDB, OpenFeign.
   - **APIs:**  
     - Endpoints for submitting reviews, retrieving reviews by movie ID, and managing reviews.

4. **Recommendation Service:**
   - **Responsibilities:**  
     - Generate rule-based movie recommendations using data from the User Profile, Movie Catalog, and Review Services.
     - Apply business rules like matching genres, high review scores, or user watch history patterns.
   - **Technologies:**  
     - Spring Boot Web, OpenFeign.
   - **APIs:**  
     - Endpoint to fetch personalized recommendations for a user.
   - **Inter-service Communication:**  
     - Uses OpenFeign to call REST endpoints of the other microservices and aggregate results.

### 3. Data Storage

- **MongoDB:**  
  - **Usage:**  
    - All microservices (User Profile, Movie Catalog, and Review Services) use MongoDB to store structured and unstructured data.
  - **Benefits:**  
    - Scalability and flexibility for dynamic schema requirements.
    - Simplified deployment by using a single NoSQL database across the platform.

### 4. Security and Authentication

- **JWT Authentication:**  
  - Implement JWT-based security in the User Profile Service.
  - Use Spring Security to secure endpoints across microservices.
  - Ensure token verification on each request, especially when using OpenFeign for inter-service calls.

### 5. API Documentation

- **Swagger Integration:**  
  - Use Swagger (e.g., Springfox or SpringDoc) in every microservice.
  - Automatically generate API documentation for all endpoints, making it easier for developers and consumers to understand the API.

### 6. Inter-Service Communication with OpenFeign

- **Why OpenFeign?**  
  - Provides a declarative way to define REST clients.
  - Reduces boilerplate code compared to manually handling REST calls.
  
- **Usage:**  
  - Each service (especially the Recommendation Service) will define Feign clients to call endpoints in the User Profile, Movie Catalog, and Review Services.
  - Configure Feign to handle error scenarios, load balancing (if needed later), and circuit breaking.

### 7. Implementation and Deployment Considerations

- **Project Structure:**  
  - Organize the project into a multi-module Maven project with separate modules for each microservice:
    ```
    movie-recommendation-platform/
    ├── user-service/
    ├── movie-catalog-service/
    ├── review-service/
    └── recommendation-service/
    ```
  
- **Testing:**  
  - Write unit and integration tests for each microservice.
  - Consider using JUnit, Mockito, and integration testing frameworks that support MongoDB.

- **Deployment:**  
  - Containerize services using Docker for consistent deployment.
  - Consider orchestration tools (like Kubernetes) when scaling up.

---

## Steps to Create the Microservices Using Spring Initializr

Below are detailed steps for setting up each microservice with the necessary dependencies.

### 1. User Profile Service

**Project Name:** user-service

**Dependencies to Include:**
- Spring Web
- Spring Data MongoDB
- Spring Security
- JWT Library (e.g., jjwt)
- OpenFeign
- Swagger (Springfox or SpringDoc)

**Recommended Package Structure:**
```
com.example.userservice
├── config        // JWT security and Feign client configuration
├── controller    // REST controllers for registration, login, profile management, and watch history
├── model         // Domain models for User, Role, etc.
├── repository    // MongoDB repositories for user data
├── service       // Business logic for user management and authentication
└── util          // Utility classes (e.g., JWT token provider)
```

### 2. Movie Catalog Service

**Project Name:** movie-catalog-service

**Dependencies to Include:**
- Spring Web
- Spring Data MongoDB
- OpenFeign
- Swagger
- (Optional) WebClient dependency for external API calls

**Recommended Package Structure:**
```
com.example.moviecatalogservice
├── config        // Feign client and WebClient configurations
├── controller    // REST controllers for movie retrieval and search
├── model         // Domain models for Movie
├── repository    // MongoDB repositories for movie data
├── service       // Business logic for managing movie data
└── client        // Classes to integrate with external APIs (e.g., TMDB API)
```

### 3. Review & Ratings Service

**Project Name:** review-service

**Dependencies to Include:**
- Spring Web
- Spring Data MongoDB
- OpenFeign
- Swagger

**Recommended Package Structure:**
```
com.example.reviewservice
├── config        // Feign client configuration if required
├── controller    // REST controllers for review submissions and queries
├── model         // Domain models for Review
├── repository    // MongoDB repositories for reviews
├── service       // Business logic for managing reviews
└── dto           // Data transfer objects for review data (if needed)
```

### 4. Recommendation Service

**Project Name:** recommendation-service

**Dependencies to Include:**
- Spring Web
- OpenFeign
- Swagger

**Recommended Package Structure:**
```
com.example.recommendationservice
├── config        // Configuration for Feign clients
├── controller    // REST controller for recommendation endpoints
├── service       // Business logic for generating rule-based recommendations
├── client        // Feign client classes to call user-service, movie-catalog-service, and review-service endpoints
└── model         // DTOs or domain models for recommendations
```

---

## Final Notes

- **Security:**  
  - The User Profile Service will be responsible for issuing and validating JWT tokens.  
  - Other services should verify the token as needed, particularly when processing calls via OpenFeign.

- **Inter-Service Communication:**  
  - OpenFeign should be used to simplify communication between services. Define a Feign client interface in the Recommendation Service for each dependency service (User, Movie, Review).

- **API Documentation:**  
  - Swagger integration in every microservice will facilitate understanding and testing of all endpoints.

- **Future Enhancements:**  
  - As the system evolves, consider adding an API Gateway or Eureka Server for better service discovery and routing, but these are out of scope for the current version.

This detailed PRD and the step-by-step guide for setting up your microservices using Spring Initializr should give Cursor AI all the necessary context to generate a fully functional rule-based Movie Recommendation Platform using Spring Boot with MongoDB, OpenFeign, and Swagger integration.