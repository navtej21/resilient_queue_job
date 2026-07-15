package com.example.weather_api_service.controller;

import com.example.weather_api_service.dto.ProductionEntryRequest;
import com.example.weather_api_service.entities.ProductionEntry;
import com.example.weather_api_service.entities.ProductionOrder;
import com.example.weather_api_service.repo.ProductionEntryRepo;
import com.example.weather_api_service.repo.ProductionOrderRepo;
import lombok.RequiredArgsConstructor;
import org.aspectj.apache.bcel.classfile.annotation.RuntimeTypeAnnos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/production-entries")
@RequiredArgsConstructor
public class ProductionEntryController {

    private final ProductionEntryRepo productionEntryRepo;

    private final ProductionOrderRepo productionOrderRepo;




    @PostMapping
    public ResponseEntity<String> createProductionEntry(@RequestBody ProductionEntryRequest productionEntryRequest) {
        ProductionEntry productionEntry=new ProductionEntry();
        ProductionOrder productionOrder=productionOrderRepo.findById(productionEntryRequest.getProductionId()).orElseThrow(()->{
            throw new RuntimeException("No Product Found");
        });
        productionEntry.setProductionDate(LocalDate.now());
        productionEntry.setQuantityProduced(Integer.parseInt(productionEntryRequest.getQuantityProduced()+""));
        productionEntry.setProductionOrder(productionOrder);
        productionEntryRepo.save(productionEntry);
        return ResponseEntity.ok().body("Created The Production Entry");
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductionEntry> getProductionEntryById(@PathVariable("id") Long id ){

        ProductionEntry productionEntry=productionEntryRepo.findById(id).orElse(null);
        return ResponseEntity.ok().body(productionEntry);
    }

}
