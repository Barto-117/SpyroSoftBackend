package com.example.SpyroSoft.model;

public class FuelData {
    public String fuel;

    public Double perc;

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public Double getPerc() {
        return perc;
    }

    public void setPerc(Double perc) {
        this.perc = perc;
    }

    public FuelData(String fuel, Double perc){
        this.fuel = fuel;
        this.perc = perc;
    }
}
