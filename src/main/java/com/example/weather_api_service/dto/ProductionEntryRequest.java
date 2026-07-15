package com.example.weather_api_service.dto;


import lombok.Builder;
import lombok.Data;

@Data
public class ProductionEntryRequest {

    private Long productionId;
    private Long quantityProduced;
}
