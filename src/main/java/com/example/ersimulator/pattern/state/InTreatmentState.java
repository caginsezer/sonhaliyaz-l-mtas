package com.example.ersimulator.pattern.state;

import com.example.ersimulator.model.Patient;

public class InTreatmentState implements PatientState {
    @Override
    public void nextState(Patient patient) {
        patient.setCurrentStateStr("DISCHARGED");
    }

    @Override
    public void printStatus() {
        System.out.println("Durum: Tedavi Ediliyor");
    }
}
