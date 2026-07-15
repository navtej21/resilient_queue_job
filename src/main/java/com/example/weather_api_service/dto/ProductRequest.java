package com.example.weather_api_service.dto;

import com.example.weather_api_service.enums.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    private String customerName;

    private String productName;

    private Integer quantity;

    private BigDecimal materialRequiredPerUnit;

    private Long materialId;
}
