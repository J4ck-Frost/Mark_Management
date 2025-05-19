package com.examManagement.MarkManagementService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class MarkManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarkManagementServiceApplication.class, args);
	}

}
