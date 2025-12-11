package com.example.SpyroSoft.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EnergyMixApi {
    private final WebClient webClient;

    public EnergyMixApi(WebClient webClient) {
        this.webClient = webClient;
    }


}
