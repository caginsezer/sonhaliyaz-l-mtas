package com.example.ersimulator.pattern.state;

import com.example.ersimulator.model.Patient;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  STATE DESENİ — Concrete State 2: Karantina Durumu              ║
// ║  PatientState interface'ini implement eder.                     ║
// ║  nextState() çağrıldığında: QUARANTINED → IN_TREATMENT          ║
// ║  Salgın modunda bulaşıcı hastalar bu duruma düşer.              ║
// ║  Facade'daki assignDoctorToPatient() metodu bu sınıfı kullanır. ║
// ╚══════════════════════════════════════════════════════════════════╝
public class QuarantinedState implements PatientState {
    
    @Override
    public void nextState(Patient patient) {
        // Karantinadaki hasta tedaviye alınır
        patient.setCurrentStateStr("IN_TREATMENT");
    }

    @Override
    public void printStatus() {
        System.out.println("Durum: Karantinada İzole Edildi");
    }
}
