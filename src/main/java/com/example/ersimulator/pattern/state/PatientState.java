package com.example.ersimulator.pattern.state;

import com.example.ersimulator.model.Patient;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  STATE DESENİ — Interface (Sözleşme)                            ║
// ║  Bu interface tüm durum sınıflarının uyması gereken kuralı      ║
// ║  tanımlar: "nextState ve printStatus metodun olacak"            ║
// ║  Implement eden concrete sınıflar:                              ║
// ║  WaitingState, QuarantinedState, InTreatmentState, DischargedState ║
// ║  Context: Patient nesnesi | Client: EmergencyRoomFacade         ║
// ╚══════════════════════════════════════════════════════════════════╝
public interface PatientState {
    void nextState(Patient patient);  // Hastayı bir sonraki duruma geçir
    void printStatus();               // Hastanın mevcut durumunu konsola yazdır
}
