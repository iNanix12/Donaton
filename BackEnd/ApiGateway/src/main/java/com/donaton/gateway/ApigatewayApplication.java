package com.donaton.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

	//Topología de puertos 
	// 9090 - API Gateway  (nuestro servicio actual)
	// 8086 - Donacion Service
	// 8087 - Logistica Service
	// 8088 - Necesidades en Terreno Service


@SpringBootApplication
public class ApigatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApigatewayApplication.class, args);
	}

}
