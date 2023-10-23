package com.student.management.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StudentManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentManagementServiceApplication.class, args);
	}
}
