package com.research.ai_analysis_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AiAnalysisServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiAnalysisServiceApplication.class, args);
	}

}
