package com.recommendationservice.client;

import com.recommendationservice.model.Movie;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "moviecatalogservice", url = "${moviecatalogservice.url}")
public interface MovieCatalogServiceClient {

    @GetMapping("/api/movies/{id}")
    Movie getMovieById(@PathVariable("id") String id);
    
    @GetMapping("/api/movies")
    List<Movie> getAllMovies();
    
    @GetMapping("/api/movies/search")
    List<Movie> searchMoviesByGenres(@RequestParam("genres") List<String> genres);
} 