package com.example.ersimulator.pattern.decorator;

// ─── DECORATOR DESENİ — Concrete Decorator 5: Ağrı Kesici (+50₺) ───
// Sarı Kod hastalara ve şikayeti "ağrı" içeren hastalara uygulanır.
public class PainkillerDecorator extends TreatmentDecorator {
    public PainkillerDecorator(Treatment decoratedTreatment) {
        super(decoratedTreatment); // İçine sardığı nesneyi üst sınıfa gönder
    }

    @Override
    public String getDescription() {
        return super.getDescription() + " + Ağrı Kesici"; // Açıklamaya ekle
    }

    @Override
    public double getCost() {
        return super.getCost() + 50.0; // Fiyata 50₺ ekle
    }
}
