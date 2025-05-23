package com.examManagement.MarkManagementService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableCaching
@SpringBootApplication
public class MarkManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarkManagementServiceApplication.class, args);
	}

}
