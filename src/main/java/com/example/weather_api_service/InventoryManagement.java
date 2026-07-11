package com.example.weather_api_service;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class InventoryManagement {
	public static void main(String[] args) {
		SpringApplication.run(InventoryManagement.class,args);
	}
}