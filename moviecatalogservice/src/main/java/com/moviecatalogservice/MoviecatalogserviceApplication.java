package com.moviecatalogservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MoviecatalogserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoviecatalogserviceApplication.class, args);
	}

}
