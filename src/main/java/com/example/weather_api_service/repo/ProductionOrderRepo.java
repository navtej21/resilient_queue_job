package com.example.weather_api_service.repo;

import com.example.weather_api_service.entities.ProductionOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionOrderRepo extends JpaRepository<ProductionOrder, Long> {
}
