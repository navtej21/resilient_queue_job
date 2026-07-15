package com.example.weather_api_service.response_dto;

import com.example.weather_api_service.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductionOrderResponse {

    private Long productionOrderId;
    private String customerName;
    private String productName;
    private Integer quantity;
    private String materialName;
    private OrderStatus orderStatus;
}