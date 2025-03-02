package com.recommendationservice.service;

import com.recommendationservice.model.Movie;
import com.recommendationservice.model.MovieRecommendation;

import java.util.List;

public interface RecommendationService {
    MovieRecommendation getRecommendationsForUser(String userId);
    List<Movie> getRecommendationsByGenre(String userId);
    List<Movie> getRecommendationsByRating(String userId);
    List<Movie> getRecommendationsBySimilarUsers(String userId);
} 