package com.examManagement.CandidateManagementService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CandidateManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CandidateManagementServiceApplication.class, args);
	}

}
