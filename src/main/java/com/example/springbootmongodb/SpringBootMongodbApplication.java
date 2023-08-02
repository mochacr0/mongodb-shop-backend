package com.example.springbootmongodb;

import com.example.springbootmongodb.service.DataInitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.repository.Update;

import java.time.LocalDateTime;

@Slf4j
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class SpringBootMongodbApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SpringBootMongodbApplication.class, args);
		context.getBean(DataInitService.class).init();
	}

}
