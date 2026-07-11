package com.example.weather_api_service.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long inventoryId;
    private BigDecimal availableQuantity;
    private LocalDateTime lastUpdated;

    @OneToOne
    @JoinColumn(name = "material_id")
    private Material material;
}
