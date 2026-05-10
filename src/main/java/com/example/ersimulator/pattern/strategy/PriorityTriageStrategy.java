package com.example.ersimulator.pattern.strategy;

import com.example.ersimulator.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PriorityTriageStrategy implements TriageStrategy {
    
    // Triage önceliğine göre sıralama: Kırmızı > Sarı > Yeşil
    @Override
    public List<Patient> sortPatients(List<Patient> patients) {
        return patients.stream()
                .sorted((p1, p2) -> {
                    int p1Urgency = getUrgencyValue(p1.getUrgencyLevel());
                    int p2Urgency = getUrgencyValue(p2.getUrgencyLevel());
                    
                    if (p1Urgency == p2Urgency) {
                        return p1.getArrivalTime().compareTo(p2.getArrivalTime());
                    }
                    return Integer.compare(p1Urgency, p2Urgency);
                })
                .collect(Collectors.toList());
    }

    private int getUrgencyValue(String level) {
        if (level == null) return 3;
        switch (level.toUpperCase()) {
            case "RED": return 1;
            case "YELLOW": return 2;
            case "GREEN": return 3;
            default: return 4;
        }
    }
}
