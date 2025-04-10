package com.recommendationservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.recommendationservice.client.MovieCatalogServiceClient;
import com.recommendationservice.client.ReviewServiceClient;
import com.recommendationservice.client.UserServiceClient;
import com.recommendationservice.model.Movie;
import com.recommendationservice.model.MovieRecommendation;
import com.recommendationservice.model.Review;
import com.recommendationservice.model.User;

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
        
        // Fetch recommendations from all strategies
        List<Movie> genreBasedRecommendations = getRecommendationsByGenre(userId);
        logger.info("Retrieved {} genre-based recommendations", genreBasedRecommendations.size());
        
        List<Movie> ratingBasedRecommendations = getRecommendationsByRating(userId);
        logger.info("Retrieved {} rating-based recommendations", ratingBasedRecommendations.size());
        
        List<Movie> similarUserRecommendations = getRecommendationsBySimilarUsers(userId);
        logger.info("Retrieved {} similar-user-based recommendations", similarUserRecommendations.size());
        
        // Track movie scores - use a weighted scoring approach
        Map<String, MovieScore> movieScores = new HashMap<>();
        
        // Add genre-based recommendations (weight: 1.0)
        addToScores(movieScores, genreBasedRecommendations, 1.0, "genre");
        
        // Add rating-based recommendations (weight: 1.2)
        addToScores(movieScores, ratingBasedRecommendations, 1.2, "rating");
        
        // Add similar-user recommendations (weight: 1.5)
        addToScores(movieScores, similarUserRecommendations, 1.5, "similar-users");
        
        // Sort by score and get top 10
        List<MovieScore> topMovieScores = movieScores.values().stream()
                .sorted(Comparator.comparing(MovieScore::getTotalScore).reversed())
                .limit(10)
                .collect(Collectors.toList());
        
        List<Movie> topRecommendations = topMovieScores.stream()
                .map(MovieScore::getMovie)
                .collect(Collectors.toList());
        
        // Create recommendation object with reasons
        MovieRecommendation recommendation = new MovieRecommendation(userId, topRecommendations);
        
        // Add recommendation reasons
        for (MovieScore movieScore : topMovieScores) {
            Movie movie = movieScore.getMovie();
            String reason = generateRecommendationReason(movie, movieScore.getScoresByStrategy());
            recommendation.addRecommendationReason(movie.getId(), reason);
        }
        
        logger.info("Final top 10 recommendations count: {}", topRecommendations.size());
        return recommendation;
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
        try {
            logger.info("Generating recommendations based on similar users for user: {}", userId);
            
            // 1. Get target user's reviews
            List<Review> userReviews = reviewServiceClient.getReviewsByUserId(userId);
            if (userReviews.isEmpty()) {
                logger.info("User {} has no reviews, cannot find similar users", userId);
                return Collections.emptyList();
            }
            
            // Create a map of the user's movieId -> rating
            Map<String, Integer> userRatings = userReviews.stream()
                    .collect(Collectors.toMap(Review::getMovieId, Review::getRating));
            logger.info("User {} has rated {} movies", userId, userRatings.size());
            
            // 2. Get all reviews to find similar users
            // In a real-world scenario, we would limit this query to avoid loading all reviews
            // We could use a database query to find similar users directly
            List<Review> allReviews = new ArrayList<>();
            for (String movieId : userRatings.keySet()) {
                allReviews.addAll(reviewServiceClient.getReviewsByMovieId(movieId));
            }
            
            // 3. Find users who rated the same movies
            Map<String, Map<String, Integer>> userMovieRatings = new HashMap<>();
            for (Review review : allReviews) {
                String reviewUserId = review.getUserId();
                if (!reviewUserId.equals(userId)) {
                    userMovieRatings.computeIfAbsent(reviewUserId, k -> new HashMap<>())
                            .put(review.getMovieId(), review.getRating());
                }
            }
            
            // 4. Calculate user similarity scores
            Map<String, Double> similarityScores = new HashMap<>();
            for (Map.Entry<String, Map<String, Integer>> entry : userMovieRatings.entrySet()) {
                String otherUserId = entry.getKey();
                Map<String, Integer> otherUserRatings = entry.getValue();
                
                // Calculate similarity based on common movies
                double similarity = calculateUserSimilarity(userRatings, otherUserRatings);
                if (similarity > 0) {
                    similarityScores.put(otherUserId, similarity);
                }
            }
            
            logger.info("Found {} similar users for user {}", similarityScores.size(), userId);
            
            // 5. Get the watch history of the target user
            List<String> watchHistory = userServiceClient.getWatchHistory(userId);
            Set<String> watchedMovieIds = new HashSet<>(watchHistory);
            
            // 6. Find all movie IDs rated highly by similar users
            Map<String, Double> movieScores = new HashMap<>();
            for (Map.Entry<String, Double> entry : similarityScores.entrySet()) {
                String similarUserId = entry.getKey();
                double similarityScore = entry.getValue();
                
                // Get reviews from similar user
                List<Review> similarUserReviews = reviewServiceClient.getReviewsByUserId(similarUserId);
                
                // Only consider highly rated movies (rating >= 4)
                for (Review review : similarUserReviews) {
                    if (review.getRating() >= 4 && !watchedMovieIds.contains(review.getMovieId())) {
                        movieScores.merge(review.getMovieId(), similarityScore * review.getRating(), Double::sum);
                    }
                }
            }
            
            // 7. Sort movies by score and get the top ones
            List<String> recommendedMovieIds = movieScores.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .limit(20)  // Get top 20 to have a good pool
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            
            logger.info("Found {} movies recommended by similar users", recommendedMovieIds.size());
            
            // 8. Fetch movie details for these recommendations
            List<Movie> recommendedMovies = new ArrayList<>();
            for (String movieId : recommendedMovieIds) {
                try {
                    Movie movie = movieCatalogServiceClient.getMovieById(movieId);
                    recommendedMovies.add(movie);
                } catch (Exception e) {
                    logger.error("Error fetching movie with ID {}: {}", movieId, e.getMessage());
                }
            }
            
            return recommendedMovies;
        } catch (Exception e) {
            logger.error("Error generating similar-users recommendations: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Helper method to calculate similarity between users
    private double calculateUserSimilarity(Map<String, Integer> userRatings1, Map<String, Integer> userRatings2) {
        // Find common movies
        Set<String> commonMovies = new HashSet<>(userRatings1.keySet());
        commonMovies.retainAll(userRatings2.keySet());
        
        // If no common movies, users are not similar
        if (commonMovies.isEmpty()) {
            return 0;
        }
        
        // Calculate Pearson correlation coefficient
        double sum1 = 0, sum2 = 0, sum1Sq = 0, sum2Sq = 0, pSum = 0;
        for (String movieId : commonMovies) {
            int rating1 = userRatings1.get(movieId);
            int rating2 = userRatings2.get(movieId);
            
            sum1 += rating1;
            sum2 += rating2;
            sum1Sq += rating1 * rating1;
            sum2Sq += rating2 * rating2;
            pSum += rating1 * rating2;
        }
        
        int n = commonMovies.size();
        double num = pSum - (sum1 * sum2 / n);
        double den = Math.sqrt((sum1Sq - (sum1 * sum1) / n) * (sum2Sq - (sum2 * sum2) / n));
        
        if (den == 0) return 0;
        
        double r = num / den;
        // Convert to a similarity score between 0 and 1
        return (r + 1) / 2.0;
    }

    // Generate a human-readable reason for recommendation
    private String generateRecommendationReason(Movie movie, Map<String, Double> scoresByStrategy) {
        StringBuilder reason = new StringBuilder();
        
        // Find the strategy with the highest score
        String topStrategy = scoresByStrategy.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("");
        
        switch (topStrategy) {
            case "genre":
                reason.append("This movie matches your preferred genres (")
                      .append(String.join(", ", movie.getGenres()))
                      .append(")");
                break;
            case "rating":
                reason.append("This movie is highly rated by other users");
                break;
            case "similar-users":
                reason.append("Users with similar taste enjoyed this movie");
                break;
            default:
                reason.append("This movie matches your preferences");
        }
        
        // If there are multiple high-scoring strategies, mention them
        List<String> otherStrategies = scoresByStrategy.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(topStrategy) && entry.getValue() > 0.5)
                .map(entry -> {
                    if ("genre".equals(entry.getKey())) return "matches your genres";
                    if ("rating".equals(entry.getKey())) return "highly rated";
                    if ("similar-users".equals(entry.getKey())) return "liked by similar users";
                    return entry.getKey();
                })
                .collect(Collectors.toList());
        
        if (!otherStrategies.isEmpty()) {
            reason.append(" and is also ").append(String.join(" and ", otherStrategies));
        }
        
        return reason.toString();
    }

    // Helper class to track movie scores from different recommendation strategies
    private static class MovieScore {
        private final Movie movie;
        private final Map<String, Double> scores = new HashMap<>();
        
        public MovieScore(Movie movie) {
            this.movie = movie;
        }
        
        public void addScore(String strategy, double score) {
            scores.put(strategy, score);
        }
        
        public double getTotalScore() {
            return scores.values().stream().mapToDouble(Double::doubleValue).sum();
        }
        
        public Movie getMovie() {
            return movie;
        }
        
        public Map<String, Double> getScoresByStrategy() {
            return new HashMap<>(scores);
        }
    }

    // Helper method to add movies to the score map with appropriate weights
    private void addToScores(Map<String, MovieScore> scoreMap, List<Movie> movies, double weight, String strategy) {
        for (int i = 0; i < movies.size(); i++) {
            Movie movie = movies.get(i);
            // Calculate score based on position (higher ranked = higher score)
            double score = weight * (1.0 - ((double) i / movies.size()));
            
            scoreMap.computeIfAbsent(movie.getId(), k -> new MovieScore(movie))
                    .addScore(strategy, score);
        }
    }
} 