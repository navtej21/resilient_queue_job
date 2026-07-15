package com.example.weather_api_service.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "production_entry")
@Data
public class ProductionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long production_entry_id;

    private Integer quantityProduced;
    private LocalDate productionDate;

    @ManyToOne
    @JoinColumn(name = "production_order_id")
    private ProductionOrder productionOrder;
}
