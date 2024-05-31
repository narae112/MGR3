package com.MGR;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class MgrApplication {

	public static void main(String[] args) {
		SpringApplication.run(MgrApplication.class, args);

	}

}
