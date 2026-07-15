package com.example.weather_api_service.controller;


import com.example.weather_api_service.dto.ProductRequest;
import com.example.weather_api_service.entities.Material;
import com.example.weather_api_service.entities.ProductionOrder;
import com.example.weather_api_service.enums.OrderStatus;
import com.example.weather_api_service.repo.MaterialRepo;
import com.example.weather_api_service.repo.ProductionOrderRepo;
import com.example.weather_api_service.response_dto.ProductionOrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/order")
public class ProductionOrderController {


    private final ProductionOrderRepo productionOrderRepo;
    private final MaterialRepo materialRepo;

    public ProductionOrderController(ProductionOrderRepo productionOrderRepo,MaterialRepo materialRepo){
        this.productionOrderRepo=productionOrderRepo;
        this.materialRepo=materialRepo;
    }




    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody ProductRequest productionOrderRequest){

        Material material=materialRepo.findByMaterialId(productionOrderRequest.getMaterialId()).orElseThrow(()->{
            return new RuntimeException("No Material Found");
        });
        ProductionOrder productionOrder=new ProductionOrder();
        productionOrder.setProductName(productionOrderRequest.getProductName());
        productionOrder.setQuantity(productionOrderRequest.getQuantity());
        productionOrder.setOrderStatus(OrderStatus.CREATED);
        productionOrder.setCustomerName(productionOrderRequest.getCustomerName());
        productionOrder.setMaterialRequiredPerUnit(productionOrderRequest.getMaterialRequiredPerUnit());
        productionOrder.setQuantity(productionOrder.getQuantity());
        productionOrder.setMaterial(material);
        productionOrderRepo.save(productionOrder);
        return ResponseEntity.ok().body("Created The Product");

    }


    @GetMapping
    public ResponseEntity<List<ProductionOrderResponse>> getListOfOrders() {

        List<ProductionOrder> orders = productionOrderRepo.findAll();

        List<ProductionOrderResponse> response = orders.stream()
                .map(order -> {

                    ProductionOrderResponse dto = new ProductionOrderResponse();

                    dto.setProductionOrderId(order.getProductionOrderId());
                    dto.setProductName(order.getProductName());
                    dto.setQuantity(order.getQuantity());
                    dto.setOrderStatus(order.getOrderStatus());
                    dto.setMaterialName(order.getMaterial().getMaterialName());

                    return dto;
                })
                .toList();

        return ResponseEntity.ok(response);
    }



    @GetMapping("/{id}")
    public ResponseEntity<ProductionOrderResponse> getOrderInfoById(@PathVariable("id") Long id){

        ProductionOrder  productionOrder=productionOrderRepo.findById(id).orElseThrow(()->{
            throw new RuntimeException("No OrderItem Found");
        });
        ProductionOrderResponse productionOrderResponse=new ProductionOrderResponse();
        productionOrderResponse.setOrderStatus(productionOrder.getOrderStatus());
        productionOrderResponse.setProductionOrderId(productionOrder.getProductionOrderId());
        productionOrderResponse.setCustomerName(productionOrder.getCustomerName());
        productionOrderResponse.setMaterialName(productionOrder.getMaterial().getMaterialName());
        productionOrderResponse.setQuantity(productionOrder.getQuantity());
        productionOrderResponse.setProductName(productionOrder.getProductName());
        return ResponseEntity.ok().body(productionOrderResponse);
    }
}
