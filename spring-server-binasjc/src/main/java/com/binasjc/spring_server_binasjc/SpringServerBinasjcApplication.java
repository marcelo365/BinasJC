package com.binasjc.spring_server_binasjc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.binasjc.spring_server_binasjc")
@EnableAutoConfiguration
public class SpringServerBinasjcApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringServerBinasjcApplication.class, args);
	}

}