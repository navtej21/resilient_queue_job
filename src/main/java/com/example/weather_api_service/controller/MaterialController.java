package com.example.weather_api_service.controller;


import com.example.weather_api_service.entities.Material;
import com.example.weather_api_service.repo.MaterialRepo;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class MaterialController {


    private final MaterialRepo materialRepo;



    @GetMapping
    public ResponseEntity<List<Material>> getAllMaterials(){
        List<Material> materials= materialRepo.findAll();
        return ResponseEntity.ok().body(materials);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Material> getMaterialById(@PathVariable("id") Long id){
        Material material=materialRepo.findById(id).orElse(null);
        return ResponseEntity.ok().body(material);
    }


    @PostMapping
    public ResponseEntity<Material> createMaterial(@RequestBody Material material){
        materialRepo.save(material);
        return ResponseEntity.ok().build();
    }
}
