package com.example.ersimulator.pattern.decorator;

// ─── DECORATOR DESENİ — Concrete Decorator 2: Serum (+250₺) ───
// Kırmızı ve Sarı Kod hastalara uygulanır.
public class SerumDecorator extends TreatmentDecorator {
    public SerumDecorator(Treatment decoratedTreatment) {
        super(decoratedTreatment); // İçine sardığı nesneyi üst sınıfa gönder
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Serum (IV Fluid)"; // Açıklamaya ekle
    }

    @Override
    public double getCost() {
        return super.getCost() + 250.0; // Fiyata 250₺ ekle
    }
}
