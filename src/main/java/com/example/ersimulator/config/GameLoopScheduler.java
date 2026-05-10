package com.example.ersimulator.config;

import com.example.ersimulator.model.Patient;
import com.example.ersimulator.pattern.facade.EmergencyRoomFacade;
import com.example.ersimulator.service.SimulationManager;
import com.example.ersimulator.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameLoopScheduler {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private EmergencyRoomFacade facade;

    @Autowired
    private SimulationManager simulationManager;

    @Scheduled(fixedRate = 1000)
    public void simulationTick() {
        if (!simulationManager.isSimulationRunning()) {
            return;
        }

        List<Patient> treatingPatients = patientRepository.findByCurrentStateStr("IN_TREATMENT");
        for (Patient p : treatingPatients) {
            int remaining = p.getTreatmentTimeRemaining();
            if (remaining > 0) {
                p.setTreatmentTimeRemaining(remaining - 1);
                patientRepository.save(p);
            } else {
                facade.dischargePatient(p.getId());
            }
        }
        
        List<Patient> waitingPatients = patientRepository.findByCurrentStateStr("WAITING");
        if (!waitingPatients.isEmpty()) {
            facade.assignDoctorToNextPatient();
        }
    }
}
