package com.example.ersimulator.pattern.decorator;

public abstract class TreatmentDecorator implements Treatment {
    protected Treatment decoratedTreatment;

    public TreatmentDecorator(Treatment decoratedTreatment) {
        this.decoratedTreatment = decoratedTreatment;
    }

    @Override
    public String getDescription() {
        return decoratedTreatment.getDescription();
    }

    @Override
    public double getCost() {
        return decoratedTreatment.getCost();
    }
}
