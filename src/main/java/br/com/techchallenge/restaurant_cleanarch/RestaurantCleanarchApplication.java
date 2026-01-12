package br.com.techchallenge.restaurant_cleanarch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RestaurantCleanarchApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestaurantCleanarchApplication.class, args);
	}

}
