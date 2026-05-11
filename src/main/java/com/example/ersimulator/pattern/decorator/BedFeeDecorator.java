package com.example.ersimulator.pattern.decorator;

public class BedFeeDecorator extends TreatmentDecorator {

    public BedFeeDecorator(Treatment customTreatment) {
        super(customTreatment);
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Yatak Ücreti";
    }

    @Override
    public double getCost() {
        return super.getCost() + 500.0;
    }
}
