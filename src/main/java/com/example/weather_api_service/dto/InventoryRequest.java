package com.example.weather_api_service.dto;


import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class InventoryRequest {
    private Long materialId;
    private Long availableQuantity;
}
