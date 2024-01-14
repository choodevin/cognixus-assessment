package com.cognixus.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class AssessmentApplication {
	public static void main(String[] args) {
		SpringApplication.run(AssessmentApplication.class, args);
	}
}
