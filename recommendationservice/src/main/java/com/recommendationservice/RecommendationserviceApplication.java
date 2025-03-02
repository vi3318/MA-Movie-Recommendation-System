package com.recommendationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RecommendationserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecommendationserviceApplication.class, args);
	}

}
