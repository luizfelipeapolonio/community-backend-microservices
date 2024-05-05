package com.felipe.communityservicediscovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class CommunityServiceDiscoveryApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityServiceDiscoveryApplication.class, args);
	}

}
