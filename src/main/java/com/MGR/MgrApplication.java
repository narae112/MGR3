package com.MGR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MgrApplication {

	public static void main(String[] args) {
		SpringApplication.run(MgrApplication.class, args);
	}

}
