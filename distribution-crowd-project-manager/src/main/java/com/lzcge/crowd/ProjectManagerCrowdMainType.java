package com.lzcge.crowd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @description:
 * @author: lzcge
 * @create: 2020-03-29
 **/

@EnableFeignClients
@EnableEurekaClient
@SpringBootApplication
public class ProjectManagerCrowdMainType {

	public static void main(String[] args) {
		SpringApplication.run(ProjectManagerCrowdMainType.class, args);
	}
}
