package com.example.ersimulator.pattern.decorator;

public class SerumDecorator extends TreatmentDecorator {
    public SerumDecorator(Treatment decoratedTreatment) {
        super(decoratedTreatment);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Serum (IV Fluid)";
    }

    @Override
    public double getCost() {
        return super.getCost() + 250.0;
    }
}
