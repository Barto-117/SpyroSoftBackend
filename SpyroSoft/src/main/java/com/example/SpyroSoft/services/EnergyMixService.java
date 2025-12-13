package com.example.SpyroSoft.services;

import com.example.SpyroSoft.model.FuelData;
import com.example.SpyroSoft.model.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.util.*;

@Service
public class EnergyMixService {
    private static final Logger log = LoggerFactory.getLogger(EnergyMixService.class);
    private final WebClient webClient;

    public EnergyMixService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<JsonNode> getData(String from, String to) {
        String url = String.format("https://api.carbonintensity.org.uk/generation/%sT00:00Z/%sT23:30Z", from, to);
        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::processData);
    }

    public JsonNode processData(JsonNode data){
        List<Interval> intervals = new ArrayList<>();
        JsonNode dataNode = data.get("data");
        for (JsonNode node : dataNode){

            List<FuelData> fuelData = new ArrayList<>();
            JsonNode generationmix = node.get("generationmix");
            for (JsonNode fuel : generationmix){
                fuelData.add(new FuelData(fuel.get("fuel").asString(), fuel.get("perc").asDouble()));
            }
            intervals.add(new Interval(node.get("from").asString(), fuelData));
        }
        Map<String, List<Double>> values1 = new HashMap<>();
        Map<String, Double> average1 = new HashMap<>();
        Map<String, List<Double>> values2 = new HashMap<>();
        Map<String, Double> average2 = new HashMap<>();
        Map<String, List<Double>> values3 = new HashMap<>();
        Map<String, Double> average3 = new HashMap<>();
        int count = 0;
        for (Interval interval : intervals){
            for (FuelData fuelData : interval.getFuelData()){
                if (count < 48) {
                    if (!values1.containsKey(fuelData.getFuel())) {
                        values1.put(fuelData.getFuel(), new ArrayList<>());
                    }
                    values1.get(fuelData.getFuel()).add(fuelData.getPerc());
                }
                if (count < 96) {
                    if (!values2.containsKey(fuelData.getFuel())) {
                        values2.put(fuelData.getFuel(), new ArrayList<>());
                    }
                    values2.get(fuelData.getFuel()).add(fuelData.getPerc());
                }
                if (count < 144) {
                    if (!values3.containsKey(fuelData.getFuel())) {
                        values3.put(fuelData.getFuel(), new ArrayList<>());
                    }
                    values3.get(fuelData.getFuel()).add(fuelData.getPerc());
                }
            }
            count++;
        }
        for (Map.Entry<String, List<Double>> map : values1.entrySet()){
            double sum = 0;
            for (double val : map.getValue()) {
                sum += val;
            }
            average1.put(map.getKey(), sum / map.getValue().size());
        }
        for (Map.Entry<String, List<Double>> map : values2.entrySet()){
            double sum = 0;
            for (double val : map.getValue()) {
                sum += val;
            }
            average2.put(map.getKey(), sum / map.getValue().size());
        }
        for (Map.Entry<String, List<Double>> map : values3.entrySet()){
            double sum = 0;
            for (double val : map.getValue()) {
                sum += val;
            }
            average3.put(map.getKey(), sum / map.getValue().size());
        }

        return getJsonNodes(average1, average2, average3);
    }

    private static ObjectNode getJsonNodes(Map<String, Double> average1, Map<String, Double> average2, Map<String, Double> average3) {
        Set<String> cleanEnergy = new HashSet<>(Arrays.asList("biomass", "nuclear", "hydro", "wind", "solar"));
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        ArrayNode day1Array = mapper.createArrayNode();
        double cleanEnergy1 = 0;
        for (Map.Entry<String, Double> entry : average1.entrySet()) {
            ObjectNode fuelNode = mapper.createObjectNode();
            fuelNode.put("fuel", entry.getKey());
            fuelNode.put("perc", entry.getValue());
            day1Array.add(fuelNode);
            if (cleanEnergy.contains(entry.getKey())) {
                cleanEnergy1 += entry.getValue();
            }
        }
        ObjectNode day1Node = mapper.createObjectNode();
        day1Node.set("mix", day1Array);
        day1Node.put("cleanEnergyPercentage", cleanEnergy1);
        node.set("day1", day1Node);

        double cleanEnergy2 = 0;
        ArrayNode day2Array = mapper.createArrayNode();
        for (Map.Entry<String, Double> entry : average2.entrySet()) {
            ObjectNode fuelNode = mapper.createObjectNode();
            fuelNode.put("fuel", entry.getKey());
            fuelNode.put("perc", entry.getValue());
            day2Array.add(fuelNode);
            if (cleanEnergy.contains(entry.getKey())) {
                cleanEnergy2 += entry.getValue();
            }
        }
        ObjectNode day2Node = mapper.createObjectNode();
        day2Node.set("mix", day2Array);
        day2Node.put("cleanEnergyPercentage", cleanEnergy2);
        node.set("day2", day2Node);

        double cleanEnergy3 = 0;
        ArrayNode day3Array = mapper.createArrayNode();
        for (Map.Entry<String, Double> entry : average3.entrySet()) {
            ObjectNode fuelNode = mapper.createObjectNode();
            fuelNode.put("fuel", entry.getKey());
            fuelNode.put("perc", entry.getValue());
            day3Array.add(fuelNode);
            if (cleanEnergy.contains(entry.getKey())) {
                cleanEnergy3 += entry.getValue();
            }
        }
        ObjectNode day3Node = mapper.createObjectNode();
        day3Node.set("mix", day3Array);
        day3Node.put("cleanEnergyPercentage", cleanEnergy3);
        node.set("day3", day3Node);;
        return node;
    }
}
