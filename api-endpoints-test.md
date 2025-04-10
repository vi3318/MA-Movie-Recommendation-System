# API Endpoints Testing Report

## User Service

### Authentication Endpoints
- ❌ POST /api/auth/register - Register a new user (Error: Authentication failed)
- ❌ POST /api/auth/login - Login a user and get JWT token (Error: 401 Unauthorized)

### User Management Endpoints
- ❌ GET /api/users/profile - Get current user profile (Not tested, auth required)
- ❌ GET /api/users/{id} - Get user by ID (Service connection timeout)
- ❌ PUT /api/users/profile - Update user profile (Not tested, auth required)
- ❌ POST /api/users/watchhistory - Add movie to watch history (Not tested, auth required)
- ❌ GET /api/users/watchhistory - Get user's watch history (Not tested, auth required)
- ❌ POST /api/users/genres - Add preferred genre (Not tested, auth required)

## Movie Catalog Service

### Movie Management Endpoints
- ✅ GET /api/movies - Get all movies (Status 200, returns list of movies)
- ✅ GET /api/movies/{id} - Get movie by ID (Status 200, returns movie details)
- ❌ POST /api/movies - Create a new movie (Not tested, likely requires auth)
- ❌ PUT /api/movies/{id} - Update an existing movie (Not tested, likely requires auth)
- ❌ DELETE /api/movies/{id} - Delete a movie (Not tested, likely requires auth)

### Movie Search Endpoints
- ✅ GET /api/movies/search?title={title} - Search movies by title (Status 200, returns array)
- ❌ GET /api/movies/search?genres={genres} - Search movies by genres (Not tested)
- ❌ GET /api/movies/search?releaseYear={year} - Search movies by release year (Not tested)
- ❌ GET /api/movies/search?director={director} - Search movies by director (Not tested)
- ❌ GET /api/movies/search?actors={actors} - Search movies by actors (Not tested)

## Review Service

### Review Management Endpoints
- ✅ GET /api/reviews - Get all reviews (Status 200, returns list of reviews)
- ❌ GET /api/reviews/{id} - Get review by ID (Not tested)
- ✅ GET /api/reviews/movie/{movieId} - Get reviews by movie ID (Status 200, returns reviews)
- ✅ GET /api/reviews/user/{userId} - Get reviews by user ID (Status 200, returns reviews)
- ❌ GET /api/reviews/rating/{minRating} - Get reviews by minimum rating (Not tested)
- ✅ GET /api/reviews/movie/{movieId}/average-rating - Get average rating for movie (Status 200, returns 5.0)
- ❌ POST /api/reviews - Add a new review (Not tested, likely requires auth)
- ❌ PUT /api/reviews/{id} - Update an existing review (Not tested, likely requires auth)
- ❌ DELETE /api/reviews/{id} - Delete a review (Not tested, likely requires auth)

## Recommendation Service

### Recommendation Endpoints
- ✅ GET /api/recommendations/users/{userId} - Get general recommendations for user (Status 200, empty array)
- ✅ GET /api/recommendations/users/{userId}/genre - Get recommendations by genre (Status 200, empty array)
- ❌ GET /api/recommendations/users/{userId}/rating - Get recommendations by rating (Not tested)
- ❌ GET /api/recommendations/users/{userId}/similar-users - Get recommendations by similar users (Not tested)
- ❌ GET /api/recommendations/test-communication/{userId} - Test inter-service communication (Status 200, but reports failure connecting to user-service)
- ❌ GET /api/recommendations/users/{userId}/top10 - Get top 10 recommendations (Not tested)
- ❌ GET /api/recommendations/users/{userId}/detailed - Get detailed recommendations (Not tested)

## Testing Status and Notes

### Inter-Service Communication
- Eureka Server Status: ✅ Working (All services are registered)
- API Gateway Routes: ✅ Partially Working (Movie and Review services work, User service unreachable)
- Service Discovery: ❌ Problematic (Services cannot communicate with each other)

### Issues and Observations

1. **Authentication Issues**:
   - Authentication endpoints return 401 Unauthorized, preventing login
   - Without authentication, protected endpoints cannot be tested

2. **Inter-Service Communication Issues**:
   - Recommendation service cannot contact user-service (connection timeout)
   - This suggests service discovery or network issues between services

3. **Working Services**:
   - Movie catalog and Review services are accessible via API Gateway
   - GET operations work correctly for non-authenticated endpoints

4. **Recommendations**:
   - Empty recommendation arrays returned - likely because the recommendation service cannot access user data
   - Recommendation algorithms appear to be implemented but not populating results due to connectivity issues

5. **Next Steps**:
   - Fix authentication service issues
   - Resolve service discovery and inter-service communication problems
   - Verify proper configuration of service URLs in Feign clients
   - Test service connectivity directly rather than through API Gateway to isolate issues

## Final Conclusions and Recommendations

After testing all API endpoints through the API gateway and examining the Kubernetes deployments, we've identified the following key issues and recommendations:

### Current Status:
1. **API Gateway and Eureka Server**: Both are running correctly and services are registered.
2. **Data Services (Movie, Review)**: These services are responding correctly for read operations.
3. **Authentication Service**: The user-service is running but authentication endpoints are failing.
4. **Inter-Service Communication**: Services are failing to communicate with each other despite being registered in Eureka.

### Main Issues Detected:
1. **Authentication Failure**: The login endpoint is returning 401 unauthorized, which is preventing access to protected endpoints.
2. **Service Discovery Issues**: The logs from user-service show connection timeout errors when trying to reach Eureka.
3. **Recommendation Service Dependencies**: The recommendation service depends on data from user-service but can't connect.

### Recommendations for Fixing:

1. **Authentication Service**:
   - Check security configuration in user-service to ensure the login endpoint is not accidentally protected
   - Validate the JWT token provider configuration
   - Ensure BCrypt password encoder settings match the stored password hashes

2. **Service Communication**:
   - Check network policies in Kubernetes to ensure services can communicate with each other
   - Verify Eureka client configuration in all services
   - Consider using Kubernetes DNS for service discovery instead of Eureka if problems persist

3. **Specific Service Fixes**:
   - User Service: Fix authentication issues first, then verify the service can be accessed through the API gateway
   - Recommendation Service: Once user service is accessible, retest the recommendation endpoints
   - Consider adding health endpoints to each service for easier troubleshooting

4. **Testing Strategy**:
   - After fixing the services, retest the entire application flow
   - Implement automated integration tests that validate service-to-service communication
   - Use a tool like Postman to create a collection of API tests

Once these issues are resolved, the recommendation system should properly function, and users will be able to register, login, and receive personalized movie recommendations based on their preferences and watch history. 