package com.moviecatalogservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.moviecatalogservice.model.Movie;

@Repository
public interface MovieRepository extends MongoRepository<Movie, String> {
    List<Movie> findByTitleContainingIgnoreCase(String title);
    
    @Query("{ 'genres': { $in: ?0 } }")
    List<Movie> findByGenres(List<String> genres);
    
    List<Movie> findByReleaseYear(int releaseYear);
    
    List<Movie> findByDirectorContainingIgnoreCase(String director);
    
    @Query("{ 'actors': { $in: ?0 } }")
    List<Movie> findByActors(List<String> actors);
} 