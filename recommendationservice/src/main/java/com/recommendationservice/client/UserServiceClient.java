package com.recommendationservice.client;

import com.recommendationservice.config.FeignClientConfig;
import com.recommendationservice.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "userservice", url = "${userservice.url}", configuration = FeignClientConfig.class)
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    User getUserById(@PathVariable("id") String id);
    
    @GetMapping("/api/users/{id}/watchhistory")
    List<String> getWatchHistory(@PathVariable("id") String userId);
} 