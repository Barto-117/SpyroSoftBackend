package com.example.SpyroSoft.services;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class EnergyMixService {
    private final WebClient webClient;

    public EnergyMixService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<JsonNode> getData(String from, String to) {
        String url = String.format("https://api.carbonintensity.org.uk/generation/%s/%s", from, to);
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
}
