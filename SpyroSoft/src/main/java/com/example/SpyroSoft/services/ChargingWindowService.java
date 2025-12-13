package com.example.SpyroSoft.services;

import com.example.SpyroSoft.model.FuelData;
import com.example.SpyroSoft.model.Interval;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.time.LocalDate;
import java.util.*;

@Service
public class ChargingWindowService {
    private final WebClient webClient;

    public ChargingWindowService(WebClient webClient) {
        this.webClient = webClient;
    }


    public Mono<JsonNode> getData(String from, String to, int duration) {
        LocalDate toDate = LocalDate.parse(to);
        LocalDate fromDate = LocalDate.parse(from).plusDays(1);
        String url = String.format("https://api.carbonintensity.org.uk/generation/%sT00:00Z/%sT23:30Z", fromDate, toDate);
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(data -> processData(data, duration));
    }

    public JsonNode processData(JsonNode data, int duration){
        List<Interval> intervals = new ArrayList<>();
        JsonNode dataNode = data.get("data");
        Set<String> cleanEnergy = new HashSet<>(Arrays.asList("biomass", "nuclear", "hydro", "wind", "solar"));
        List<Double> cleanEnergyPerc = new ArrayList<>();
        for (JsonNode node : dataNode){
            JsonNode generationmix = node.get("generationmix");
            double sum = 0;
            for (JsonNode fuel : generationmix){
                if(cleanEnergy.contains(fuel.get("fuel").asString())){
                    sum += fuel.get("perc").asDouble();
                }
            }
            cleanEnergyPerc.add(sum);
        }


        int intervalsPerDuration = duration * 2;
        double maxAverage = 0;
        int bestStartIndex = 0;

        for (int i = 0; i <= cleanEnergyPerc.size() - (duration * 2); i++) {
            double sum = 0;
            for (int j = i; j < i + intervalsPerDuration; j++) {
                sum += cleanEnergyPerc.get(j);
            }
            double average = sum / intervalsPerDuration;

            if (average > maxAverage) {
                maxAverage = average;
                bestStartIndex = i;
            }
        }

        String startDate = dataNode.get(bestStartIndex).get("from").asString();
        String endDate = dataNode.get(bestStartIndex + intervalsPerDuration - 1).get("to").asString();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode result = mapper.createObjectNode();

        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("averageCleanEnergyPer", maxAverage);
        return result;
    }
}
