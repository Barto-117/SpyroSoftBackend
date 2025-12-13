package com.example.SpyroSoft.controllers;

import com.example.SpyroSoft.services.EnergyMixService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@RestController
public class EnergyMixController {
    private final EnergyMixService energyMixService;


    public EnergyMixController(EnergyMixService energyMixService) {
        this.energyMixService = energyMixService;
    }

    @GetMapping("/{from}/{to}")
    @CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
    public Mono<JsonNode> getData(@PathVariable String from, @PathVariable String to) {
        return energyMixService.getData(from, to);
    }
}
