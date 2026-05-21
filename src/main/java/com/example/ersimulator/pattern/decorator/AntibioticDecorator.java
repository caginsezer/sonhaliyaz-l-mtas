package com.example.ersimulator.pattern.decorator;

// ─── DECORATOR DESENİ — Concrete Decorator 4: Antibiyotik (+120₺) ───
// Şikayeti "ateş" veya "enfeksiyon" içeren hastalara uygulanır.
public class AntibioticDecorator extends TreatmentDecorator {
    public AntibioticDecorator(Treatment decoratedTreatment) {
        super(decoratedTreatment); // İçine sardığı nesneyi üst sınıfa gönder
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Antibiyotik"; // Açıklamaya ekle
    }

    @Override
    public double getCost() {
        return super.getCost() + 120.0; // Fiyata 120₺ ekle
    }
}
