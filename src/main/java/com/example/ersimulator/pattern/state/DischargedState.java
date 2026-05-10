package com.example.ersimulator.pattern.state;

import com.example.ersimulator.model.Patient;

public class DischargedState implements PatientState {
    @Override
    public void nextState(Patient patient) {
        System.out.println("Hasta zaten taburcu edildi.");
    }

    @Override
    public void printStatus() {
        System.out.println("Durum: Taburcu Edildi");
    }
}
