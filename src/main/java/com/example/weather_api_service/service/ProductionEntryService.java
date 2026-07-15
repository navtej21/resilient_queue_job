package com.example.weather_api_service.service;


import com.example.weather_api_service.dto.MaterialRequest;
import com.example.weather_api_service.dto.ProductionEntryRequest;
import com.example.weather_api_service.entities.Inventory;
import com.example.weather_api_service.entities.Material;
import com.example.weather_api_service.entities.ProductionOrder;
import com.example.weather_api_service.repo.InventoryRepo;
import com.example.weather_api_service.repo.MaterialRepo;
import com.example.weather_api_service.repo.ProductionEntryRepo;
import com.example.weather_api_service.repo.ProductionOrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


// this is the main business logic which is the crux of the problem
@Service
@RequiredArgsConstructor
public class ProductionEntryService {

    private final ProductionEntryRepo productionEntryRepo;
    private final ProductionOrderRepo productionOrderRepo;
    private final MaterialRepo materialRepo;
    private final InventoryRepo inventoryRepo;


    public String garmentProductionEntry(ProductionEntryRequest productionRequest){

        // find the production-id

        ProductionOrder productionOrder=productionOrderRepo.findById(productionRequest.getProductionId()).orElseThrow(()->{
            throw new RuntimeException("No Production Order");
        });

        // find the material
        Material currentMaterial=productionOrder.getMaterial();

        // find the inventory;
        Inventory currentInventory=inventoryRepo.findByMaterial(currentMaterial).orElseThrow(()->{
            throw new RuntimeException("No Inventory of the given material found");
        });



    }
}
