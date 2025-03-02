package com.recommendationservice.controller;

import com.recommendationservice.client.MovieCatalogServiceClient;
import com.recommendationservice.client.ReviewServiceClient;
import com.recommendationservice.client.UserServiceClient;
import com.recommendationservice.model.Movie;
import com.recommendationservice.model.MovieRecommendation;
import com.recommendationservice.model.Review;
import com.recommendationservice.model.User;
import com.recommendationservice.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private RecommendationService recommendationService;
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    @Autowired
    private MovieCatalogServiceClient movieCatalogServiceClient;
    
    @Autowired
    private ReviewServiceClient reviewServiceClient;

    @GetMapping("/users/{userId}")
    public ResponseEntity<MovieRecommendation> getRecommendationsForUser(@PathVariable String userId) {
        MovieRecommendation recommendations = recommendationService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/users/{userId}/genre")
    public ResponseEntity<List<Movie>> getRecommendationsByGenre(@PathVariable String userId) {
        List<Movie> recommendations = recommendationService.getRecommendationsByGenre(userId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/users/{userId}/rating")
    public ResponseEntity<List<Movie>> getRecommendationsByRating(@PathVariable String userId) {
        List<Movie> recommendations = recommendationService.getRecommendationsByRating(userId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/users/{userId}/similar-users")
    public ResponseEntity<List<Movie>> getRecommendationsBySimilarUsers(@PathVariable String userId) {
        List<Movie> recommendations = recommendationService.getRecommendationsBySimilarUsers(userId);
        return ResponseEntity.ok(recommendations);
    }
    
    @GetMapping("/test-communication/{userId}")
    public ResponseEntity<Map<String, Object>> testInterServiceCommunication(@PathVariable String userId) {
        logger.info("Testing inter-service communication for user: {}", userId);
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Test User Service communication
            logger.info("Fetching user data from User Service");
            User user = userServiceClient.getUserById(userId);
            result.put("user", user);
            
            // Test Movie Catalog Service communication
            logger.info("Fetching movies from Movie Catalog Service");
            List<Movie> allMovies = movieCatalogServiceClient.getAllMovies();
            result.put("totalMovies", allMovies.size());
            
            if (!allMovies.isEmpty()) {
                Movie firstMovie = allMovies.get(0);
                result.put("sampleMovie", firstMovie);
                
                // Test Review Service communication
                logger.info("Fetching reviews for movie: {}", firstMovie.getId());
                List<Review> movieReviews = reviewServiceClient.getReviewsByMovieId(firstMovie.getId());
                result.put("movieReviews", movieReviews);
                
                Double avgRating = reviewServiceClient.getAverageRatingForMovie(firstMovie.getId());
                result.put("averageRating", avgRating);
            }
            
            result.put("success", true);
            result.put("message", "Inter-service communication test successful");
            logger.info("Inter-service communication test completed successfully");
            
        } catch (Exception e) {
            logger.error("Error during inter-service communication test: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("message", "Inter-service communication test failed");
        }
        
        return ResponseEntity.ok(result);
    }
} 