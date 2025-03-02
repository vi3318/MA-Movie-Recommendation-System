package com.reviewservice.service;

import com.reviewservice.client.MovieCatalogServiceClient;
import com.reviewservice.client.UserServiceClient;
import com.reviewservice.model.Review;
import com.reviewservice.repository.ReviewRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServiceImpl.class);

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private MovieCatalogServiceClient movieCatalogServiceClient;
    
    @Autowired
    private UserServiceClient userServiceClient;

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Review> getReviewById(String id) {
        return reviewRepository.findById(id);
    }

    @Override
    public List<Review> getReviewsByMovieId(String movieId) {
        return reviewRepository.findByMovieId(movieId);
    }

    @Override
    public List<Review> getReviewsByUserId(String userId) {
        return reviewRepository.findByUserId(userId);
    }

    @Override
    public Review addReview(Review review) {
        // Validate that the movie exists
        try {
            logger.info("Validating movie existence for movieId: {}", review.getMovieId());
            movieCatalogServiceClient.getMovieById(review.getMovieId());
        } catch (FeignException e) {
            logger.error("Movie validation failed: {}", e.getMessage());
            throw new IllegalArgumentException("Cannot add review for non-existent movie: " + review.getMovieId());
        }
        
        // Validate that the user exists
        try {
            logger.info("Validating user existence for userId: {}", review.getUserId());
            userServiceClient.getUserById(review.getUserId());
        } catch (FeignException e) {
            logger.error("User validation failed: {}", e.getMessage());
            throw new IllegalArgumentException("Cannot add review for non-existent user: " + review.getUserId());
        }
        
        logger.info("Validation successful, adding review for movie: {} by user: {}", 
                review.getMovieId(), review.getUserId());
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(String id, Review reviewDetails) {
        Optional<Review> review = reviewRepository.findById(id);
        if (review.isPresent()) {
            Review existingReview = review.get();
            
            // If movieId is changing, validate the new movie exists
            if (!existingReview.getMovieId().equals(reviewDetails.getMovieId())) {
                try {
                    logger.info("Validating movie existence for updated movieId: {}", reviewDetails.getMovieId());
                    movieCatalogServiceClient.getMovieById(reviewDetails.getMovieId());
                } catch (FeignException e) {
                    logger.error("Movie validation failed during update: {}", e.getMessage());
                    throw new IllegalArgumentException("Cannot update review to non-existent movie: " + reviewDetails.getMovieId());
                }
            }
            
            // If userId is changing, validate the new user exists
            if (!existingReview.getUserId().equals(reviewDetails.getUserId())) {
                try {
                    logger.info("Validating user existence for updated userId: {}", reviewDetails.getUserId());
                    userServiceClient.getUserById(reviewDetails.getUserId());
                } catch (FeignException e) {
                    logger.error("User validation failed during update: {}", e.getMessage());
                    throw new IllegalArgumentException("Cannot update review to non-existent user: " + reviewDetails.getUserId());
                }
            }
            
            existingReview.setMovieId(reviewDetails.getMovieId());
            existingReview.setUserId(reviewDetails.getUserId());
            existingReview.setRating(reviewDetails.getRating());
            existingReview.setComment(reviewDetails.getComment());
            
            logger.info("Updating review: {}", id);
            return reviewRepository.save(existingReview);
        }
        return null;
    }

    @Override
    public void deleteReview(String id) {
        logger.info("Deleting review: {}", id);
        reviewRepository.deleteById(id);
    }

    @Override
    public List<Review> getReviewsByMinimumRating(int rating) {
        return reviewRepository.findByRatingGreaterThanEqual(rating);
    }

    @Override
    public Double getAverageRatingForMovie(String movieId) {
        List<Review> reviews = reviewRepository.findByMovieId(movieId);
        if (reviews.isEmpty()) {
            return 0.0;
        }
        
        double sum = reviews.stream()
                .mapToInt(Review::getRating)
                .sum();
        
        return sum / reviews.size();
    }
} 