package com.example.weather_api_service.entities;


import com.example.weather_api_service.enums.AlertStatus;
import jakarta.persistence.*;

import javax.annotation.processing.Generated;
import java.time.LocalDateTime;

@Entity
@Table(name = "alert")
public class AlertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long alertId;

    private String message;

    @Enumerated(EnumType.STRING)
    private AlertStatus alertStatus;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;


}
