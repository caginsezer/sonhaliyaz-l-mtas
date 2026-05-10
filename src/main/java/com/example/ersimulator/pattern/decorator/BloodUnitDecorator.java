package com.example.ersimulator.pattern.decorator;

public class BloodUnitDecorator extends TreatmentDecorator {
    public BloodUnitDecorator(Treatment decoratedTreatment) {
        super(decoratedTreatment);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Kan Nakli Ünitesi";
    }

    @Override
    public double getCost() {
        return super.getCost() + 1500.0;
    }
}
