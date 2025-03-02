package com.recommendationservice.client;

import com.recommendationservice.model.Review;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "reviewservice", url = "${reviewservice.url}")
public interface ReviewServiceClient {

    @GetMapping("/api/reviews/movie/{movieId}")
    List<Review> getReviewsByMovieId(@PathVariable("movieId") String movieId);
    
    @GetMapping("/api/reviews/user/{userId}")
    List<Review> getReviewsByUserId(@PathVariable("userId") String userId);
    
    @GetMapping("/api/reviews/movie/{movieId}/average-rating")
    Double getAverageRatingForMovie(@PathVariable("movieId") String movieId);
    
    @GetMapping("/api/reviews/rating/{minRating}")
    List<Review> getReviewsByMinimumRating(@PathVariable("minRating") int minRating);
} 