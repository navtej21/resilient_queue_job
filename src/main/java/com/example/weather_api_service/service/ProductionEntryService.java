package com.example.weather_api_service.service;


import com.example.weather_api_service.dto.ProductionEntryRequest;
import com.example.weather_api_service.entities.Inventory;
import com.example.weather_api_service.entities.Material;
import com.example.weather_api_service.entities.ProductionEntry;
import com.example.weather_api_service.entities.ProductionOrder;
import com.example.weather_api_service.enums.OrderStatus;
import com.example.weather_api_service.repo.InventoryRepo;
import com.example.weather_api_service.repo.ProductionEntryRepo;
import com.example.weather_api_service.repo.ProductionOrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;


// this is the main business logic which is the crux of the problem
@Service
@RequiredArgsConstructor
@Transactional
public class ProductionEntryService {

    private final ProductionEntryRepo productionEntryRepo;
    private final ProductionOrderRepo productionOrderRepo;
    private final InventoryRepo inventoryRepo;

    public void garmentProductionEntry(ProductionEntryRequest productionRequest) {

        // 1. Find the Production Order
        ProductionOrder productionOrder = productionOrderRepo
                .findById(productionRequest.getProductionId())
                .orElseThrow(() -> new RuntimeException("Production Order not found"));

        // 2. Get the Material used for this order
        Material currentMaterial = productionOrder.getMaterial();

        // 3. Find the Inventory of the material
        Inventory currentInventory = inventoryRepo
                .findByMaterial(currentMaterial)
                .orElseThrow(() -> new RuntimeException("Inventory not found for the given material"));

        // 4. Calculate material consumption
        BigDecimal consumption = BigDecimal
                .valueOf(productionRequest.getQuantityProduced())
                .multiply(productionOrder.getMaterialRequiredPerUnit());

        System.out.println("consumption"+consumption);

        // 5. Calculate remaining inventory
        BigDecimal remaining = currentInventory
                .getAvailableQuantity()
                .subtract(consumption);

        System.out.println("remaining"+remaining);

        // 6. Reject if inventory is insufficient
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient inventory to complete today's production.");
        }

        // 7. Update inventory
        currentInventory.setAvailableQuantity(remaining);

        // 8. Create Production Entry
        ProductionEntry productionEntry = new ProductionEntry();
        productionEntry.setProductionOrder(productionOrder);
        productionEntry.setQuantityProduced(productionRequest.getQuantityProduced().intValue());
        productionEntry.setProductionDate(LocalDate.now());


        // 9.a update the production Order Status as Completed
        if(productionEntry.getQuantityProduced()==0){
            productionOrder.setOrderStatus(OrderStatus.COMPLETED);
        }

        // 9. Update Production Order Status
        productionOrder.setOrderStatus(OrderStatus.IN_PROGRESS);

        // 10. Check Safety Stock
        BigDecimal safetyStock = new BigDecimal(currentMaterial.getSafetyStock());

        String message = "Production recorded successfully.";

        if (remaining.compareTo(safetyStock) < 0) {
            message += " Warning: Inventory has fallen below the safety stock level.";
        }

        // 11. Save all changes
        inventoryRepo.save(currentInventory);
        productionEntryRepo.save(productionEntry);
        productionOrderRepo.save(productionOrder);

    }
}