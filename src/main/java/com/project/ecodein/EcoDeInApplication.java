package com.project.ecodein;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@SpringBootApplication
public class EcoDeInApplication {

	public static void main (String[] args) {

		SpringApplication.run (EcoDeInApplication.class, args);

	}

	// Pageable Setting (Zero -> One)
	@Bean
	public PageableHandlerMethodArgumentResolverCustomizer customize () {

		return p -> {

			p.setOneIndexedParameters (true);

		};

	}

}
