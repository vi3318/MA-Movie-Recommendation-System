package com.recommendationservice.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {
    
    private String id;
    private String username;
    private List<String> watchHistory = new ArrayList<>();
    private Set<String> preferredGenres = new HashSet<>();
    
    // Constructors
    public User() {
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public List<String> getWatchHistory() {
        return watchHistory;
    }
    
    public void setWatchHistory(List<String> watchHistory) {
        this.watchHistory = watchHistory;
    }
    
    public Set<String> getPreferredGenres() {
        return preferredGenres;
    }
    
    public void setPreferredGenres(Set<String> preferredGenres) {
        this.preferredGenres = preferredGenres;
    }
} 