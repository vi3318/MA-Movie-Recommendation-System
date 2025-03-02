package com.reviewservice.controller;

import com.reviewservice.model.Review;
import com.reviewservice.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        logger.info("Fetching all reviews");
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReviewById(@PathVariable String id) {
        logger.info("Fetching review with id: {}", id);
        Optional<Review> review = reviewService.getReviewById(id);
        return review.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<Review>> getReviewsByMovieId(@PathVariable String movieId) {
        logger.info("Fetching reviews for movie: {}", movieId);
        List<Review> reviews = reviewService.getReviewsByMovieId(movieId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsByUserId(@PathVariable String userId) {
        logger.info("Fetching reviews by user: {}", userId);
        List<Review> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<Review>> getReviewsByMinimumRating(@PathVariable int minRating) {
        logger.info("Fetching reviews with minimum rating: {}", minRating);
        List<Review> reviews = reviewService.getReviewsByMinimumRating(minRating);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/movie/{movieId}/average-rating")
    public ResponseEntity<Double> getAverageRatingForMovie(@PathVariable String movieId) {
        logger.info("Calculating average rating for movie: {}", movieId);
        Double averageRating = reviewService.getAverageRatingForMovie(movieId);
        return ResponseEntity.ok(averageRating);
    }

    @PostMapping
    public ResponseEntity<?> addReview(@RequestBody Review review) {
        try {
            logger.info("Adding new review for movie: {} by user: {}", review.getMovieId(), review.getUserId());
            Review savedReview = reviewService.addReview(review);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReview);
        } catch (IllegalArgumentException e) {
            logger.error("Failed to add review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error adding review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while adding the review");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable String id, @RequestBody Review reviewDetails) {
        try {
            logger.info("Updating review: {}", id);
            Review updatedReview = reviewService.updateReview(id, reviewDetails);
            if (updatedReview != null) {
                return ResponseEntity.ok(updatedReview);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.error("Failed to update review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error updating review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the review");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        logger.info("Deleting review: {}", id);
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
} 