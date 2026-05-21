package com.example.ersimulator.pattern.strategy;

import com.example.ersimulator.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  STRATEGY DESENİ — Concrete Strategy 2: Geliş Sırasına Göre    ║
// ║  TriageStrategy interface'ini implement eder.                   ║
// ║  FIFO: First In First Out — ilk gelen ilk alınır.               ║
// ║  Aciliyeti görmezden gelir, sadece arrivalTime'a göre sıralar.  ║
// ║  Alternatif strateji: İleride geçiş yapmak istenirse            ║
// ║  Facade'daki @Autowired değiştirmek yeterli.                    ║
// ╚══════════════════════════════════════════════════════════════════╝
@Component
public class FifoTriageStrategy implements TriageStrategy {

    @Override
    public List<Patient> sortPatients(List<Patient> patients) {
        // Sadece geliş zamanına göre sırala — aciliyet fark etmez
        return patients.stream()
                .sorted(Comparator.comparing(Patient::getArrivalTime))
                .collect(Collectors.toList());
    }
}
