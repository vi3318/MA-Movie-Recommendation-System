package com.userservice.controller;

import com.userservice.model.User;
import com.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Don't return the password
            user.setPassword(null);
            return ResponseEntity.ok(user);
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Don't return the password
            user.setPassword(null);
            return ResponseEntity.ok(user);
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody User updatedUser) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Update only allowed fields
            if (updatedUser.getEmail() != null) {
                user.setEmail(updatedUser.getEmail());
            }
            
            if (updatedUser.getPreferredGenres() != null) {
                user.setPreferredGenres(updatedUser.getPreferredGenres());
            }
            
            User savedUser = userService.updateUser(user);
            savedUser.setPassword(null);
            return ResponseEntity.ok(savedUser);
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @PostMapping("/watchhistory")
    public ResponseEntity<?> addToWatchHistory(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String movieId = request.get("movieId");
            
            userService.addToWatchHistory(user.getId(), movieId);
            return ResponseEntity.ok("Movie added to watch history");
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @GetMapping("/watchhistory")
    public ResponseEntity<?> getWatchHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<String> watchHistory = userService.getWatchHistory(user.getId());
            return ResponseEntity.ok(watchHistory);
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }

    @PostMapping("/genres")
    public ResponseEntity<?> addPreferredGenre(@RequestBody Map<String, String> request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String genre = request.get("genre");
            
            userService.addPreferredGenre(user.getId(), genre);
            return ResponseEntity.ok("Genre added to preferences");
        }
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
    }
} 