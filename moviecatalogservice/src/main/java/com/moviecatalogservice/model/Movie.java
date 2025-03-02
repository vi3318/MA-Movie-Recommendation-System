package com.moviecatalogservice.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "movies")
public class Movie {
    
    @Id
    private String id;
    private String title;
    private String description;
    private List<String> genres = new ArrayList<>();
    private int releaseYear;
    private String director;
    private List<String> actors = new ArrayList<>();
    private String posterUrl;
    
    // Constructors
    public Movie() {
    }
    
    public Movie(String title, String description, List<String> genres, int releaseYear, 
                String director, List<String> actors, String posterUrl) {
        this.title = title;
        this.description = description;
        this.genres = genres;
        this.releaseYear = releaseYear;
        this.director = director;
        this.actors = actors;
        this.posterUrl = posterUrl;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getGenres() {
        return genres;
    }
    
    public void setGenres(List<String> genres) {
        this.genres = genres;
    }
    
    public int getReleaseYear() {
        return releaseYear;
    }
    
    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }
    
    public String getDirector() {
        return director;
    }
    
    public void setDirector(String director) {
        this.director = director;
    }
    
    public List<String> getActors() {
        return actors;
    }
    
    public void setActors(List<String> actors) {
        this.actors = actors;
    }
    
    public String getPosterUrl() {
        return posterUrl;
    }
    
    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }
} 