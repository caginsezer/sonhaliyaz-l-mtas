package com.example.ersimulator.pattern.decorator;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  DECORATOR DESENİ — Concrete Component (Temel Nesne / Çekirdek) ║
// ║  Treatment interface'ini doğrudan implement eder.               ║
// ║  Faturanın BAŞLANGIÇ noktası — her fatura buradan başlar.       ║
// ║  Benzetme: Çıplak dondurma — üstünde henüz hiçbir sos yok.     ║
// ║  Fiyat: 100₺ | Açıklama: "Temel Muayene"                       ║
// ╚══════════════════════════════════════════════════════════════════╝
public class BasicTreatment implements Treatment {
    @Override
    public String getDescription() {
        return "Temel Muayene"; // Temel açıklama — üzerine decorator'lar eklenir
    }

    @Override
    public double getCost() {
        return 100.0; // Temel fiyat: 100₺ — üzerine decorator'lar eklenir
    }
}
