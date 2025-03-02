package com.userservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    private String username;
    private String email;
    private String password;
    private Set<String> roles = new HashSet<>();
    private List<String> watchHistory = new ArrayList<>();
    private Set<String> preferredGenres = new HashSet<>();
    
    // Constructors
    public User() {
    }
    
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Set<String> getRoles() {
        return roles;
    }
    
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
    public List<String> getWatchHistory() {
        return watchHistory;
    }
    
    public void setWatchHistory(List<String> watchHistory) {
        this.watchHistory = watchHistory;
    }
    
    public void addToWatchHistory(String movieId) {
        this.watchHistory.add(movieId);
    }
    
    public Set<String> getPreferredGenres() {
        return preferredGenres;
    }
    
    public void setPreferredGenres(Set<String> preferredGenres) {
        this.preferredGenres = preferredGenres;
    }
    
    public void addPreferredGenre(String genre) {
        this.preferredGenres.add(genre);
    }
} 