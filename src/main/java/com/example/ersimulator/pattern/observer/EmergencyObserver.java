package com.example.ersimulator.pattern.observer;

import com.example.ersimulator.model.Patient;

public interface EmergencyObserver {
    void update(Patient patient);
}
