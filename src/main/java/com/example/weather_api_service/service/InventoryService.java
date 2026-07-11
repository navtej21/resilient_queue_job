package com.example.weather_api_service.service;


import com.example.weather_api_service.dto.InventoryRequest;
import com.example.weather_api_service.entities.Inventory;
import com.example.weather_api_service.entities.Material;
import com.example.weather_api_service.repo.InventoryRepo;
import com.example.weather_api_service.repo.MaterialRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepo inventoryRepo;

    @Autowired
    private MaterialRepo materialRepo;

    public void saveInventory(InventoryRequest inventoryRequest){
        if (inventoryRequest == null || inventoryRequest.getMaterialId() == null) {
            throw new IllegalArgumentException("Material ID must not be null");
        }
        Material material=materialRepo.findByMaterialId(inventoryRequest.getMaterialId()).orElseThrow(()->{
            throw new RuntimeException("No Item Present");
        });
        Inventory inventory1=new Inventory();
        inventory1.setMaterial(material);
        inventory1.setAvailableQuantity(BigDecimal.valueOf(1000));
        inventory1.setLastUpdated(LocalDateTime.now());
        inventoryRepo.save(inventory1);

    }
}
