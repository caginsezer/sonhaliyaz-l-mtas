package com.example.ersimulator.pattern.strategy;

import com.example.ersimulator.model.Patient;

import java.util.List;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  STRATEGY DESENİ — Interface (Sözleşme)                        ║
// ║  Hasta sıralama algoritmasının sözleşmesi.                     ║
// ║  "sortPatients metodun olacak" der, NASIL sıralayacağını       ║
// ║  söylemez — onu concrete sınıflar belirler.                    ║
// ║  Implement eden sınıflar: PriorityTriageStrategy, FifoTriageStrategy ║
// ║  Context + Client: EmergencyRoomFacade                         ║
// ╚══════════════════════════════════════════════════════════════════╝
public interface TriageStrategy {
    List<Patient> sortPatients(List<Patient> patients); // Hasta listesini sıralayıp döndür
}
