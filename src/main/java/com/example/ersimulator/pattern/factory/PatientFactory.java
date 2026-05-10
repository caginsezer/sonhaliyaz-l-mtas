package com.example.ersimulator.pattern.factory;

import com.example.ersimulator.model.Patient;

public class PatientFactory {

    // 1. Factory Method Pattern: Hasta yaratımı tek bir merkezden yönetiliyor.
    public static Patient createPatient(String name, int age, String complaint, String type) {
        if (type == null) {
            return null;
        }

        Patient patient = new Patient(name, age, complaint, type.toUpperCase());
        
        // GERÇEK ZAMANLI DAKİKA UYARLAMASI (1 Saniye = 1 Döngü)
        // Red: 30 Dakika = 1800 Saniye
        // Yellow: 15 Dakika = 900 Saniye
        // Green: 5 Dakika = 300 Saniye
        switch (type.toUpperCase()) {
            case "RED":
                patient.setTreatmentTimeRemaining(1800); 
                break;
            case "YELLOW":
                patient.setTreatmentTimeRemaining(900);
                break;
            case "GREEN":
                patient.setTreatmentTimeRemaining(300);
                break;
            default:
                throw new IllegalArgumentException("Unknown Patient Type: " + type);
        }
        return patient;
    }
}
