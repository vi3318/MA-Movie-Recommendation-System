package com.moviecatalogservice.service;

import java.util.List;
import java.util.Optional;

import com.moviecatalogservice.model.Movie;

public interface MovieService {
    List<Movie> getAllMovies();
    Optional<Movie> getMovieById(String id);
    Movie saveMovie(Movie movie);
    void deleteMovie(String id);
    List<Movie> searchMoviesByTitle(String title);
    List<Movie> searchMoviesByGenres(List<String> genres);
    List<Movie> searchMoviesByReleaseYear(int releaseYear);
    List<Movie> searchMoviesByDirector(String director);
    List<Movie> searchMoviesByActors(List<String> actors);
} 