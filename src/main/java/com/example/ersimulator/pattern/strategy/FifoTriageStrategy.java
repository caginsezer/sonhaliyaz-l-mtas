package com.example.ersimulator.pattern.strategy;

import com.example.ersimulator.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FifoTriageStrategy implements TriageStrategy {

    // Sadece geliş zamanına göre sıralama yapar
    @Override
    public List<Patient> sortPatients(List<Patient> patients) {
        return patients.stream()
                .sorted(Comparator.comparing(Patient::getArrivalTime))
                .collect(Collectors.toList());
    }
}
