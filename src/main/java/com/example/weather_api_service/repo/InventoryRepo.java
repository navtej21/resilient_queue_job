package com.example.weather_api_service.repo;

import com.example.weather_api_service.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepo extends JpaRepository<Inventory,Long> {
}
