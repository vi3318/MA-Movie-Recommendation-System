package com.recommendationservice.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieRecommendation {
    
    private String userId;
    private List<Movie> recommendedMovies;
    private Map<String, String> recommendationReasons;
    
    // Constructors
    public MovieRecommendation() {
        // Default constructor
    }
    
    public MovieRecommendation(String userId, List<Movie> recommendedMovies) {
        this.userId = userId;
        this.recommendedMovies = recommendedMovies;
        this.recommendationReasons = new HashMap<>();
    }
    
    public MovieRecommendation(String userId, List<Movie> recommendedMovies, Map<String, String> recommendationReasons) {
        this.userId = userId;
        this.recommendedMovies = recommendedMovies;
        this.recommendationReasons = recommendationReasons;
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public List<Movie> getRecommendedMovies() {
        return recommendedMovies;
    }
    
    public void setRecommendedMovies(List<Movie> recommendedMovies) {
        this.recommendedMovies = recommendedMovies;
    }
    
    public Map<String, String> getRecommendationReasons() {
        return recommendationReasons;
    }
    
    public void setRecommendationReasons(Map<String, String> recommendationReasons) {
        this.recommendationReasons = recommendationReasons;
    }
    
    public void addRecommendationReason(String movieId, String reason) {
        this.recommendationReasons.put(movieId, reason);
    }
} 