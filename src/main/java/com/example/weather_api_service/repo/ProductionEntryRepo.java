package com.example.weather_api_service.repo;

import com.example.weather_api_service.entities.ProductionEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionEntryRepo extends JpaRepository<ProductionEntry,Long> {
}
