package com.example.ersimulator.pattern.decorator;

// ─── DECORATOR DESENİ — Concrete Decorator 6: Aşı (+85₺) ───
// Şikayeti "ısırık" veya "kesik" içeren hastalara uygulanır.
public class VaccineDecorator extends TreatmentDecorator {
    public VaccineDecorator(Treatment decoratedTreatment) {
        super(decoratedTreatment); // İçine sardığı nesneyi üst sınıfa gönder
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Aşı"; // Açıklamaya ekle
    }

    @Override
    public double getCost() {
        return super.getCost() + 85.0; // Fiyata 85₺ ekle
    }
}
