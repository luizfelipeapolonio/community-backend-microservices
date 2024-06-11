package com.felipe.community_post_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CommunityPostServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityPostServiceApplication.class, args);
	}

}
