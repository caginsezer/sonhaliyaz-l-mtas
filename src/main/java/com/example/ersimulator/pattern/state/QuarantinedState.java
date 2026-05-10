package com.example.ersimulator.pattern.state;

import com.example.ersimulator.model.Patient;

public class QuarantinedState implements PatientState {
    @Override
    public void nextState(Patient patient) {
        patient.setCurrentStateStr("IN_TREATMENT");
    }

    @Override
    public void printStatus() {
        System.out.println("Durum: Karantinada İzole Edildi");
    }
}
