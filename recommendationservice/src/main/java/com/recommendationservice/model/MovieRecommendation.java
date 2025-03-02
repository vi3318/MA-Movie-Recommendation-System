package com.recommendationservice.model;

import java.util.List;

public class MovieRecommendation {
    
    private String userId;
    private List<Movie> recommendedMovies;
    
    // Constructors
    public MovieRecommendation() {
    }
    
    public MovieRecommendation(String userId, List<Movie> recommendedMovies) {
        this.userId = userId;
        this.recommendedMovies = recommendedMovies;
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
} 