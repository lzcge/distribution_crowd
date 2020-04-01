package com.lzcge.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class RedisCrowdMainType {

	public static void main(String[] args) {
		SpringApplication.run(RedisCrowdMainType.class, args);
	}

}