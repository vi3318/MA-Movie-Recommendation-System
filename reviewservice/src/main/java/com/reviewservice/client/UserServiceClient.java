package com.reviewservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "userservice", url = "${userservice.url}")
public interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    Object getUserById(@PathVariable("id") String id);
    
    // We only need to check if the user exists, so we can return any object type
} 