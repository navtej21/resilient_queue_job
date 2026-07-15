package com.example.weather_api_service.entities;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Table(name = "material")
@Entity
@Data
public class Material {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long materialId;

    @Column(name="materialName")
    private String materialName;

    @Column(name = "materialUnit")
    private String unit;

    @Column(name = "safetyStock")
    private BigInteger safetyStock=BigInteger.valueOf(1000);

    @OneToOne(mappedBy = "material")
    private Inventory inventory;


    @Column(name = "updatedTime")
    private LocalDateTime updatedAt=LocalDateTime.now();
}
