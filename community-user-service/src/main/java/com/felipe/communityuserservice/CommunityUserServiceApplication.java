package com.felipe.communityuserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CommunityUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityUserServiceApplication.class, args);
	}

}
