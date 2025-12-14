package com.example.SpyroSoft.controllers;

import com.example.SpyroSoft.services.ChargingWindowService;
import com.example.SpyroSoft.services.EnergyMixService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@RestController
public class ChargingWindowController {
    private final ChargingWindowService chargingWindowService;

    public ChargingWindowController(ChargingWindowService chargingWindowService) {
        this.chargingWindowService = chargingWindowService;
    }

    @GetMapping("/{from}/{to}/{duration}")
    @CrossOrigin(origins = "https://spyrosoftfrontend.onrender.com")
    public Mono<JsonNode> getData(@PathVariable String from, @PathVariable String to, @PathVariable int duration) {
        return chargingWindowService.getData(from, to, duration);
    }
}
