package com.example.ersimulator.pattern.state;

import com.example.ersimulator.model.Patient;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  STATE DESENİ — Concrete State 1: Bekleme Durumu                ║
// ║  PatientState interface'ini implement eder.                     ║
// ║  nextState() çağrıldığında: WAITING → IN_TREATMENT              ║
// ║  Facade'daki assignDoctorToPatient() metodu bu sınıfı kullanır. ║
// ╚══════════════════════════════════════════════════════════════════╝
public class WaitingState implements PatientState {
    
    @Override
    public void nextState(Patient patient) {
        // Bekleme durumundaki hasta tedaviye alınır
        patient.setCurrentStateStr("IN_TREATMENT");
    }

    @Override
    public void printStatus() {
        System.out.println("Durum: Bekliyor");
    }
}
