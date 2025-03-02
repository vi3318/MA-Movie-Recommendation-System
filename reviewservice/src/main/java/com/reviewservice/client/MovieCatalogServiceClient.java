package com.reviewservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "moviecatalogservice", url = "${moviecatalogservice.url}")
public interface MovieCatalogServiceClient {

    @GetMapping("/api/movies/{id}")
    Object getMovieById(@PathVariable("id") String id);
    
    // We only need to check if the movie exists, so we can return any object type
} 