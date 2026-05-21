package com.example.ersimulator.pattern.decorator;

// ─── DECORATOR DESENİ — Concrete Decorator 1: Yatak Ücreti (+500₺) ───
// TreatmentDecorator'ı extends eder. İçindekinin fiyatına 500₺ ekler.
// Her hastaya uygulanır (Facade'da ilk sarılan katman).
public class BedFeeDecorator extends TreatmentDecorator {

    public BedFeeDecorator(Treatment customTreatment) {
        super(customTreatment); // İçine sardığı nesneyi üst sınıfa gönder
    }

    @Override
    public String getDescription() {
        return super.getDescription() + ", Yatak Ücreti"; // İçindekinin açıklaması + kendi eklentisi
    }

    @Override
    public double getCost() {
        return super.getCost() + 500.0; // İçindekinin fiyatı + 500₺
    }
}
