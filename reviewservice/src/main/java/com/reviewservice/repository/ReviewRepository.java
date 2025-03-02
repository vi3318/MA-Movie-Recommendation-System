package com.reviewservice.repository;

import com.reviewservice.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByMovieId(String movieId);
    List<Review> findByUserId(String userId);
    List<Review> findByMovieIdAndUserId(String movieId, String userId);
    List<Review> findByRatingGreaterThanEqual(int rating);
} 