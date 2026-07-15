package com.example.weather_api_service.repo;

import com.example.weather_api_service.entities.Inventory;
import com.example.weather_api_service.entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepo extends JpaRepository<Inventory,Long> {


    // find the current material
    Optional<Inventory> findByMaterial(Material material);
}
