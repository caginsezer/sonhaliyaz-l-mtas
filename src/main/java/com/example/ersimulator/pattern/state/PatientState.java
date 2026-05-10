package com.example.ersimulator.pattern.state;

import com.example.ersimulator.model.Patient;

public interface PatientState {
    void nextState(Patient patient);
    void printStatus();
}
