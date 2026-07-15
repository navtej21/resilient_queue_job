package com.example.weather_api_service.dto;

import lombok.Data;

import java.math.BigInteger;

@Data
public class MaterialRequest {
    private String materialName;
    private String unit;
    private BigInteger safetyStock;
}
