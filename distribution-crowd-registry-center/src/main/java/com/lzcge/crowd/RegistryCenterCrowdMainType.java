package com.lzcge.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class RegistryCenterCrowdMainType {

	public static void main(String[] args) {
		SpringApplication.run(RegistryCenterCrowdMainType.class, args);
	}

}