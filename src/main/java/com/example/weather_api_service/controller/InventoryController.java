package com.example.weather_api_service.controller;


import com.example.weather_api_service.dto.InventoryRequest;
import com.example.weather_api_service.entities.Inventory;
import com.example.weather_api_service.repo.InventoryRepo;
import com.example.weather_api_service.service.InventoryService;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {


    private final InventoryRepo inventoryRepo;
    private final InventoryService inventoryService;


    @PostMapping
    public ResponseEntity<String> postInventory(@RequestBody InventoryRequest inventory){
        if (inventory == null || inventory.getMaterialId() == null) {
            return ResponseEntity.badRequest().body("Material ID (materialId or id) must not be null");
        }
        inventoryService.saveInventory(inventory);
        return ResponseEntity.ok().body("SUCCESS CREATED");
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory(){
        List<Inventory> inventories=inventoryRepo.findAll();
        return ResponseEntity.ok().body(inventories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inventory> getInventoryById(@PathVariable("id") Long id){
        Inventory inventory=inventoryRepo.findById(id).orElse(null);
        return ResponseEntity.ok().body(inventory);
    }
}
