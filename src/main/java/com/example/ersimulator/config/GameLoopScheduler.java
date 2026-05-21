package com.example.ersimulator.config;

import com.example.ersimulator.model.Patient;
import com.example.ersimulator.pattern.facade.EmergencyRoomFacade;
import com.example.ersimulator.service.SimulationManager;
import com.example.ersimulator.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  GAME LOOP — OTOMATİK SİMÜLASYON DÖNGÜSÜ                      ║
// ║  Spring'in @Scheduled anotasyonuyla HER 1 SANİYEDE bir çalışır. ║
// ║  FACADE DESENİNİN 2. CLIENT'IDIR (1. client = Controller)      ║
// ║  Alt sistemlere doğrudan erişmez — Facade üzerinden çalışır.    ║
// ║  3 iş yapar:                                                    ║
// ║  1. Tedavideki hastaların süresini azaltır, bitince taburcu eder║
// ║  2. Bekleyen hastaya doktor atar                                ║
// ║  3. Dinlenen doktorların enerjisini doldurur                    ║
// ╚══════════════════════════════════════════════════════════════════╝
@Service
public class GameLoopScheduler {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private com.example.ersimulator.repository.DoctorRepository doctorRepository;

    // ─── FACADE: GameLoop da Facade'ın client'idir ───
    @Autowired
    private EmergencyRoomFacade facade;

    @Autowired
    private SimulationManager simulationManager;

    // Her 1000ms (1 saniye) = 1 "tick"
    @Scheduled(fixedRate = 1000)
    public void simulationTick() {
        // Simülasyon duraklatıldıysa hiçbir şey yapma
        if (!simulationManager.isSimulationRunning()) {
            return;
        }

        // ─── İŞ 1: Tedavideki hastaların geri sayımını azalt ───
        // Arayüzdeki "19s, 33s" gibi sayılar bu mantıkla azalır
        List<Patient> treatingPatients = patientRepository.findByCurrentStateStr("IN_TREATMENT");
        for (Patient p : treatingPatients) {
            int remaining = p.getTreatmentTimeRemaining();
            if (remaining > 0) {
                p.setTreatmentTimeRemaining(remaining - 1); // Her tick'te 1 azalt
                patientRepository.save(p);
            } else {
                // Süre bitti → FACADE üzerinden taburcu et
                // İçinde DECORATOR (fatura) + STATE (DISCHARGED geçişi) çalışır
                facade.dischargePatient(p.getId());
            }
        }
        
        // ─── İŞ 2: Bekleyen hasta varsa doktor ata ───
        // İçinde STRATEGY (sıralama) + STATE (geçiş) çalışır
        List<Patient> waitingPatients = patientRepository.findByCurrentStateStr("WAITING");
        if (!waitingPatients.isEmpty()) {
            facade.assignDoctorToNextPatient();
        }
        
        // ─── İŞ 3: Dinlenen doktorların enerjisini doldur ───
        // Her tick'te %5 enerji eklenir, %100 olunca tekrar aktif olur
        for (com.example.ersimulator.model.Doctor doc : doctorRepository.findAll()) {
            if (doc.isResting()) {
                int newEnergy = Math.min(100, doc.getEnergyLevel() + 5);
                doc.setEnergyLevel(newEnergy);
                if (newEnergy >= 100) {
                    doc.setResting(false);
                    doc.setAvailable(true); // Artık müsait
                    // Uyandığı an boşta hasta varsa hemen ata
                    facade.assignDoctorToNextPatient();
                }
                doctorRepository.save(doc);
            }
        }
    }
}
