package com.reviewservice.service;

import com.reviewservice.model.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Review> getAllReviews();
    
    Optional<Review> getReviewById(String id);
    
    List<Review> getReviewsByMovieId(String movieId);
    
    List<Review> getReviewsByUserId(String userId);
    
    Review addReview(Review review);
    
    Review updateReview(String id, Review reviewDetails);
    
    void deleteReview(String id);
    
    List<Review> getReviewsByMinimumRating(int rating);
    
    Double getAverageRatingForMovie(String movieId);
    
    // Keep this method for backward compatibility if needed
    default List<Review> findReviewsByMovieIdAndUserId(String movieId, String userId) {
        throw new UnsupportedOperationException("Method not implemented");
    }
} 