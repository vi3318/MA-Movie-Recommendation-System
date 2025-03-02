package com.moviecatalogservice.controller;

import com.moviecatalogservice.model.Movie;
import com.moviecatalogservice.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @GetMapping
    public ResponseEntity<List<Movie>> getAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        return ResponseEntity.ok(movies);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovieById(@PathVariable String id) {
        Optional<Movie> movie = movieService.getMovieById(id);
        if (movie.isPresent()) {
            return ResponseEntity.ok(movie.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found with ID: " + id);
    }

    @PostMapping
    public ResponseEntity<Movie> createMovie(@RequestBody Movie movie) {
        Movie savedMovie = movieService.saveMovie(movie);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable String id, @RequestBody Movie movie) {
        Optional<Movie> existingMovie = movieService.getMovieById(id);
        if (existingMovie.isPresent()) {
            movie.setId(id);
            Movie updatedMovie = movieService.saveMovie(movie);
            return ResponseEntity.ok(updatedMovie);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found with ID: " + id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable String id) {
        Optional<Movie> existingMovie = movieService.getMovieById(id);
        if (existingMovie.isPresent()) {
            movieService.deleteMovie(id);
            return ResponseEntity.ok("Movie deleted successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Movie not found with ID: " + id);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Movie>> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) List<String> genres,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) String director,
            @RequestParam(required = false) List<String> actors) {
        
        List<Movie> movies;
        
        if (title != null && !title.isEmpty()) {
            movies = movieService.searchMoviesByTitle(title);
        } else if (genres != null && !genres.isEmpty()) {
            movies = movieService.searchMoviesByGenres(genres);
        } else if (releaseYear != null) {
            movies = movieService.searchMoviesByReleaseYear(releaseYear);
        } else if (director != null && !director.isEmpty()) {
            movies = movieService.searchMoviesByDirector(director);
        } else if (actors != null && !actors.isEmpty()) {
            movies = movieService.searchMoviesByActors(actors);
        } else {
            movies = movieService.getAllMovies();
        }
        
        return ResponseEntity.ok(movies);
    }
} 