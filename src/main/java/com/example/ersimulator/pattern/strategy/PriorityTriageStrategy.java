package com.example.ersimulator.pattern.strategy;

import com.example.ersimulator.model.Patient;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  STRATEGY DESENİ — Concrete Strategy 1: Aciliyete Göre Sıralama║
// ║  TriageStrategy interface'ini implement eder.                   ║
// ║  Sıralama: RED(1) > YELLOW(2) > GREEN(3)                       ║
// ║  Aynı aciliyetteki hastalar geliş zamanına göre sıralanır.      ║
// ║  Facade'da 3 yerde kullanılır: getWaitingPatients(),            ║
// ║  getQuarantinedPatients(), assignDoctorToNextPatient()           ║
// ╚══════════════════════════════════════════════════════════════════╝
@Component // Spring bu sınıfı Bean olarak kaydeder, Facade @Autowired ile alır
public class PriorityTriageStrategy implements TriageStrategy {
    
    @Override
    public List<Patient> sortPatients(List<Patient> patients) {
        return patients.stream()
                .sorted((p1, p2) -> {
                    // Her hastanın aciliyet değerini sayıya çevir
                    int p1Urgency = getUrgencyValue(p1.getUrgencyLevel());
                    int p2Urgency = getUrgencyValue(p2.getUrgencyLevel());
                    
                    // Aynı aciliyetteyse erken gelen önce (FIFO)
                    if (p1Urgency == p2Urgency) {
                        return p1.getArrivalTime().compareTo(p2.getArrivalTime());
                    }
                    // Farklı aciliyetteyse küçük sayı önce (RED=1 en önce)
                    return Integer.compare(p1Urgency, p2Urgency);
                })
                .collect(Collectors.toList());
    }

    // Aciliyet seviyesini sayıya çeviren yardımcı metod
    // Küçük sayı = yüksek öncelik
    private int getUrgencyValue(String level) {
        if (level == null) return 3;
        switch (level.toUpperCase()) {
            case "RED": return 1;      // Kırmızı Kod — en acil
            case "YELLOW": return 2;   // Sarı Kod — orta acil
            case "GREEN": return 3;    // Yeşil Kod — düşük acil
            default: return 4;         // Bilinmeyen — en sona
        }
    }
}
