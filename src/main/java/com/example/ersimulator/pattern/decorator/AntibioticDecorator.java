package com.example.ersimulator.pattern.decorator;

public class AntibioticDecorator extends TreatmentDecorator {
    public AntibioticDecorator(Treatment decoratedTreatment) {
        super(decoratedTreatment);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Antibiyotik";
    }

    @Override
    public double getCost() {
        return super.getCost() + 120.0;
    }
}
