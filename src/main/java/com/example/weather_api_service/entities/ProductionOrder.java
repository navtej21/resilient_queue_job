package com.example.weather_api_service.entities;


import com.example.weather_api_service.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Entity
@Table(name = "production_order")
@Data
public class ProductionOrder{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productionOrderId;

    private String customerName;
    private String productName;
    private Integer quantity;
    private BigDecimal materialRequiredPerUnit;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;


    @OneToMany(mappedBy = "productionOrder")
    private List<ProductionEntry> productionEntries;

}