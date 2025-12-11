package com.example.SpyroSoft.controllers;

import com.example.SpyroSoft.services.EnergyMixService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Mono<JsonNode> getData(@RequestParam String from, @RequestParam String to) {
        return energyMixService.getData(from, to);
    }
}
