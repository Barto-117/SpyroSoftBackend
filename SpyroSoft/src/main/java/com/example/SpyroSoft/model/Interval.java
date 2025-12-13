package com.example.SpyroSoft.model;


import tools.jackson.databind.JsonNode;
import java.util.List;

public class Interval {
    public String date;
    public List<FuelData> fuelData;

    public List<FuelData> getFuelData() {
        return fuelData;
    }

    public void setFuelData(List<FuelData> fuelData) {
        this.fuelData = fuelData;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Interval(String date, List<FuelData> fuelData){
        this.date = date;
        this.fuelData = fuelData;
    }
}
