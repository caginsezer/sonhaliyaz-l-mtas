package com.example.ersimulator.pattern.decorator;

// ─── DECORATOR DESENİ — Concrete Decorator 3: Kan Nakli (+1500₺) ───
// Sadece Kırmızı Kod hastalara uygulanır. En pahalı malzeme.
public class BloodUnitDecorator extends TreatmentDecorator {
    public BloodUnitDecorator(Treatment decoratedTreatment) {
        super(decoratedTreatment); // İçine sardığı nesneyi üst sınıfa gönder
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Kan Nakli Ünitesi"; // Açıklamaya ekle
    }

    @Override
    public double getCost() {
        return super.getCost() + 1500.0; // Fiyata 1500₺ ekle
    }
}
