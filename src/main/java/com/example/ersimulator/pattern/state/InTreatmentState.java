package com.example.ersimulator.pattern.state;

import com.example.ersimulator.model.Patient;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  STATE DESENİ — Concrete State 3: Tedavide Durumu               ║
// ║  PatientState interface'ini implement eder.                     ║
// ║  nextState() çağrıldığında: IN_TREATMENT → DISCHARGED           ║
// ║  Facade'daki dischargePatient() metodu bu sınıfı kullanır.      ║
// ╚══════════════════════════════════════════════════════════════════╝
public class InTreatmentState implements PatientState {
    
    @Override
    public void nextState(Patient patient) {
        // Tedavideki hasta taburcu edilir
        patient.setCurrentStateStr("DISCHARGED");
    }

    @Override
    public void printStatus() {
        System.out.println("Durum: Tedavi Ediliyor");
    }
}
