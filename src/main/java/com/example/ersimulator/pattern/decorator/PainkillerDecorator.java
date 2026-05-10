package com.example.ersimulator.pattern.decorator;

public class PainkillerDecorator extends TreatmentDecorator {
    public PainkillerDecorator(Treatment decoratedTreatment) {
        super(decoratedTreatment);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Ağrı Kesici";
    }

    @Override
    public double getCost() {
        return super.getCost() + 50.0;
    }
}
