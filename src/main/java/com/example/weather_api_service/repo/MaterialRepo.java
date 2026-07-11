package com.example.weather_api_service.repo;

import com.example.weather_api_service.entities.Material;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MaterialRepo extends JpaRepository<Material,Long> {

    Optional<Material> findByMaterialId(Long materialId);
}
