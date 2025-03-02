package com.userservice.service;

import com.userservice.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findById(String id);
    List<User> findAllUsers();
    User updateUser(User user);
    void deleteUser(String id);
    void addToWatchHistory(String userId, String movieId);
    List<String> getWatchHistory(String userId);
    void addPreferredGenre(String userId, String genre);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
} 