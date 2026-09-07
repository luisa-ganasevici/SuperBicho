package br.com.fiap.SuperBicho;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SuperBichoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SuperBichoApplication.class, args);
	}

}
