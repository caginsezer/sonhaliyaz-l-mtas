package com.example.ersimulator.pattern.state;

import com.example.ersimulator.model.Patient;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  STATE DESENİ — Concrete State 4: Taburcu Durumu (Terminal)     ║
// ║  PatientState interface'ini implement eder.                     ║
// ║  nextState() çağrıldığında: Hiçbir yere geçmez!                 ║
// ║  Bu bir terminal durumdur — hasta artık sistemden çıkmıştır.    ║
// ║  Güvenlik: Taburcu olan hastaya tekrar işlem yapılmasını engeller║
// ╚══════════════════════════════════════════════════════════════════╝
public class DischargedState implements PatientState {
    
    @Override
    public void nextState(Patient patient) {
        // Terminal durum: Taburcu edilen hasta başka bir duruma geçemez
        System.out.println("Hasta zaten taburcu edildi.");
    }

    @Override
    public void printStatus() {
        System.out.println("Durum: Taburcu Edildi");
    }
}
