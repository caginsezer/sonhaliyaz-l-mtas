package com.example.ersimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ErSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErSimulatorApplication.class, args);
	}

}
