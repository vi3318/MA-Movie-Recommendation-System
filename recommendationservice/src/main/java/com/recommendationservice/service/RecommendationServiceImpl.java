package com.recommendationservice.service;

import com.recommendationservice.client.MovieCatalogServiceClient;
import com.recommendationservice.client.ReviewServiceClient;
import com.recommendationservice.client.UserServiceClient;
import com.recommendationservice.model.Movie;
import com.recommendationservice.model.MovieRecommendation;
import com.recommendationservice.model.Review;
import com.recommendationservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    @Autowired
    private UserServiceClient userServiceClient;

    @Autowired
    private MovieCatalogServiceClient movieCatalogServiceClient;

    @Autowired
    private ReviewServiceClient reviewServiceClient;

    @Override
    public MovieRecommendation getRecommendationsForUser(String userId) {
        logger.info("Generating recommendations for user: {}", userId);
        
        // Combine different recommendation strategies
        List<Movie> genreBasedRecommendations = getRecommendationsByGenre(userId);
        logger.info("Retrieved {} genre-based recommendations", genreBasedRecommendations.size());
        
        List<Movie> ratingBasedRecommendations = getRecommendationsByRating(userId);
        logger.info("Retrieved {} rating-based recommendations", ratingBasedRecommendations.size());
        
        // Combine and remove duplicates
        Set<String> movieIds = new HashSet<>();
        List<Movie> combinedRecommendations = new ArrayList<>();
        
        for (Movie movie : genreBasedRecommendations) {
            if (movieIds.add(movie.getId())) {
                combinedRecommendations.add(movie);
            }
        }
        
        for (Movie movie : ratingBasedRecommendations) {
            if (movieIds.add(movie.getId())) {
                combinedRecommendations.add(movie);
            }
        }
        
        logger.info("Final combined recommendations count: {}", combinedRecommendations.size());
        return new MovieRecommendation(userId, combinedRecommendations);
    }

    @Override
    public List<Movie> getRecommendationsByGenre(String userId) {
        try {
            logger.info("Fetching user data from User Service for user: {}", userId);
            // Get user's preferred genres
            User user = userServiceClient.getUserById(userId);
            Set<String> preferredGenres = user.getPreferredGenres();
            logger.info("User {} has {} preferred genres: {}", userId, preferredGenres.size(), preferredGenres);
            
            if (preferredGenres.isEmpty()) {
                logger.info("No preferred genres found for user: {}", userId);
                return Collections.emptyList();
            }
            
            // Get user's watch history
            logger.info("Fetching watch history from User Service for user: {}", userId);
            List<String> watchHistory = userServiceClient.getWatchHistory(userId);
            Set<String> watchedMovieIds = new HashSet<>(watchHistory);
            logger.info("User {} has watched {} movies", userId, watchedMovieIds.size());
            
            // Get movies by preferred genres
            logger.info("Fetching movies by genres from Movie Catalog Service: {}", preferredGenres);
            List<Movie> moviesByGenre = movieCatalogServiceClient.searchMoviesByGenres(
                    new ArrayList<>(preferredGenres));
            logger.info("Found {} movies matching preferred genres", moviesByGenre.size());
            
            // Filter out already watched movies
            List<Movie> recommendations = moviesByGenre.stream()
                    .filter(movie -> !watchedMovieIds.contains(movie.getId()))
                    .collect(Collectors.toList());
            
            logger.info("Filtered to {} unwatched movies for recommendations", recommendations.size());
            return recommendations;
        } catch (Exception e) {
            logger.error("Error getting genre-based recommendations: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Movie> getRecommendationsByRating(String userId) {
        try {
            // Get user's watch history
            logger.info("Fetching watch history from User Service for user: {}", userId);
            List<String> watchHistory = userServiceClient.getWatchHistory(userId);
            Set<String> watchedMovieIds = new HashSet<>(watchHistory);
            logger.info("User {} has watched {} movies", userId, watchedMovieIds.size());
            
            // Get highly rated movies (rating >= 4)
            logger.info("Fetching highly rated reviews from Review Service");
            List<Review> highlyRatedReviews = reviewServiceClient.getReviewsByMinimumRating(4);
            logger.info("Found {} reviews with rating >= 4", highlyRatedReviews.size());
            
            // Get unique movie IDs from reviews
            Set<String> highlyRatedMovieIds = highlyRatedReviews.stream()
                    .map(Review::getMovieId)
                    .filter(movieId -> !watchedMovieIds.contains(movieId))
                    .collect(Collectors.toSet());
            
            logger.info("Found {} unique, unwatched, highly-rated movies", highlyRatedMovieIds.size());
            
            // Get movie details for each ID
            List<Movie> recommendedMovies = new ArrayList<>();
            for (String movieId : highlyRatedMovieIds) {
                try {
                    logger.info("Fetching movie details from Movie Catalog Service for movie: {}", movieId);
                    Movie movie = movieCatalogServiceClient.getMovieById(movieId);
                    recommendedMovies.add(movie);
                } catch (Exception e) {
                    // Skip if movie not found
                    logger.error("Error fetching movie with ID {}: {}", movieId, e.getMessage());
                }
            }
            
            logger.info("Final rating-based recommendations count: {}", recommendedMovies.size());
            return recommendedMovies;
        } catch (Exception e) {
            logger.error("Error getting rating-based recommendations: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<Movie> getRecommendationsBySimilarUsers(String userId) {
        logger.info("Similar-users recommendation strategy not yet implemented for user: {}", userId);
        // This is a placeholder for a more complex recommendation algorithm
        // In a real implementation, we would find users with similar preferences
        // and recommend movies they liked but our target user hasn't watched yet
        
        // For now, we'll just return an empty list
        return Collections.emptyList();
    }
} 