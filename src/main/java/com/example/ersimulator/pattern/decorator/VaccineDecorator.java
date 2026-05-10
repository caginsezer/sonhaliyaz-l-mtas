package com.example.ersimulator.pattern.decorator;

public class VaccineDecorator extends TreatmentDecorator {
    public VaccineDecorator(Treatment decoratedTreatment) {
        super(decoratedTreatment);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Aşı";
    }

    @Override
    public double getCost() {
        return super.getCost() + 85.0;
    }
}
