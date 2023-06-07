package com.example.springbootmongodb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.mongodb.repository.Update;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpringBootMongodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootMongodbApplication.class, args);
	}

}
