package com.example.ersimulator.pattern.decorator;

public class BasicTreatment implements Treatment {
    @Override
    public String getDescription() {
        return "Temel Muayene";
    }

    @Override
    public double getCost() {
        return 100.0;
    }
}
