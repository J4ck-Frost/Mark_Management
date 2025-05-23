package com.examManagement.ExamAdministrationService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ExamAdministrationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExamAdministrationServiceApplication.class, args);
	}

}
