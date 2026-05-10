package com.example.ersimulator.pattern.strategy;

import com.example.ersimulator.model.Patient;

import java.util.List;

public interface TriageStrategy {
    List<Patient> sortPatients(List<Patient> patients);
}
