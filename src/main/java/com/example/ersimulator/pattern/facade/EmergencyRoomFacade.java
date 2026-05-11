package com.example.ersimulator.pattern.facade;

import com.example.ersimulator.model.Doctor;
import com.example.ersimulator.model.Patient;

import com.example.ersimulator.pattern.observer.EmergencyNotifier;
import com.example.ersimulator.service.SimulationManager;
import com.example.ersimulator.service.MedicalInventoryService;
import com.example.ersimulator.pattern.state.InTreatmentState;
import com.example.ersimulator.pattern.state.PatientState;
import com.example.ersimulator.pattern.state.WaitingState;
import com.example.ersimulator.pattern.state.QuarantinedState;
import com.example.ersimulator.pattern.strategy.PriorityTriageStrategy;
import com.example.ersimulator.repository.DoctorRepository;
import com.example.ersimulator.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmergencyRoomFacade {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private EmergencyNotifier emergencyNotifier;

    @Autowired
    private PriorityTriageStrategy priorityTriageStrategy;

    @Autowired
    private MedicalInventoryService inventoryService;

    @Autowired
    private SimulationManager simulationManager;

    // 6. Facade Pattern: Tüm karmaşık işlemler burada birleşir.
    public Patient admitPatient(String name, int age, String complaint, String urgencyLevel) {
        if (!simulationManager.isSimulationRunning()) {
            throw new RuntimeException("Simulation is paused!");
        }

        // 3. Factory Pattern: Hasta oluştur
        Patient newPatient = new Patient(name, age, complaint, urgencyLevel);
        
        // Otonom Karantina Mantığı Değiştirildi: Kullanıcı Salgın (Epidemic) modunu açtıysa
        // Sadece bulaşıcı şikayeti olanlar Karantinaya düşer
        if (simulationManager.isQuarantineMode() && isInfectious(complaint)) {
            newPatient.setCurrentStateStr("QUARANTINED");
            System.out.println("🚨 Salgın Modu: " + newPatient.getName() + " karantinaya alındı.");
        } else {
            newPatient.setCurrentStateStr("WAITING");
        }
        
        patientRepository.save(newPatient);

        // 4. Observer Pattern: Kırmızı kodda bildirim yap
        if ("RED".equalsIgnoreCase(urgencyLevel)) {
            emergencyNotifier.notifyObservers(newPatient);
        }
        
        assignDoctorToPatient(newPatient);

        return newPatient;
    }

    public void assignDoctorToPatient(Patient patient) {
        if ("WAITING".equals(patient.getCurrentStateStr()) || "QUARANTINED".equals(patient.getCurrentStateStr())) {
            List<Doctor> availableDoctors = doctorRepository.findByIsAvailable(true);
            if (!availableDoctors.isEmpty()) {
                
                boolean hasSupplies = requestSuppliesForTreatment(patient.getUrgencyLevel());
                if (!hasSupplies) {
                    System.out.println("STOK YETERSİZ! " + patient.getName() + " için atama yapılamadı.");
                    return;
                }

                Doctor assigned = availableDoctors.get(0);
                
                if(assigned.isResting()) {
                    System.out.println(assigned.getName() + " dinleniyor, atama yapılamaz.");
                    return;
                }

                assigned.setAvailable(false);
                patient.setAssignedDoctor(assigned);

                // KESİN (EXACT) SÜRE ZORLAMASI (Multiplier/Tiredness mantığı iptal edildi)
                int finalTime = patient.getTreatmentTimeRemaining(); // Red=30, Yellow=15, Green=5
                patient.setTreatmentTimeRemaining(finalTime);

                // 1. State Pattern: State Değişimi
                PatientState state;
                if ("QUARANTINED".equals(patient.getCurrentStateStr())) {
                    state = new QuarantinedState();
                } else {
                    state = new WaitingState();
                }
                state.nextState(patient); // WAITING/QUARANTINED -> IN_TREATMENT
                
                doctorRepository.save(assigned);
                patientRepository.save(patient);
            }
        }
    }

    private boolean requestSuppliesForTreatment(String urgencyLevel) {
        if ("RED".equalsIgnoreCase(urgencyLevel)) {
            if (inventoryService.hasStockForRed()) {
                inventoryService.consumeForRed();
                return true;
            }
        } else if ("YELLOW".equalsIgnoreCase(urgencyLevel)) {
            if (inventoryService.hasStockForYellow()) {
                inventoryService.consumeForYellow();
                return true;
            }
        } else if ("GREEN".equalsIgnoreCase(urgencyLevel)) {
            if (inventoryService.hasStockForGreen()) {
                inventoryService.consumeForGreen();
                return true;
            }
        }
        return false;
    }

    public void dischargePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow();
        if ("IN_TREATMENT".equals(patient.getCurrentStateStr())) {
            
            // 5. Decorator Pattern: Fatura ve tedavi modülleri
            com.example.ersimulator.pattern.decorator.Treatment treatment = new com.example.ersimulator.pattern.decorator.BasicTreatment();
            
            // Her hastaya standart yatak ücreti ekliyoruz
            treatment = new com.example.ersimulator.pattern.decorator.BedFeeDecorator(treatment);
            
            String complaint = patient.getComplaint().toLowerCase();

            if ("RED".equalsIgnoreCase(patient.getUrgencyLevel())) {
                treatment = new com.example.ersimulator.pattern.decorator.SerumDecorator(
                              new com.example.ersimulator.pattern.decorator.BloodUnitDecorator(treatment));
            } else if ("YELLOW".equalsIgnoreCase(patient.getUrgencyLevel())) {
                treatment = new com.example.ersimulator.pattern.decorator.SerumDecorator(
                              new com.example.ersimulator.pattern.decorator.PainkillerDecorator(treatment));
            }

            if (complaint.contains("ateş") || complaint.contains("enfeksiyon")) {
                treatment = new com.example.ersimulator.pattern.decorator.AntibioticDecorator(treatment);
            } else if (complaint.contains("ağrı")) {
                treatment = new com.example.ersimulator.pattern.decorator.PainkillerDecorator(treatment);
            } else if (complaint.contains("ısırık") || complaint.contains("kesik")) {
                treatment = new com.example.ersimulator.pattern.decorator.VaccineDecorator(treatment);
            }

            patient.setFinalBill(String.format("Malzemeler: %s | Fatura: %.2f TL", treatment.getDescription(), treatment.getCost()));

            // 1. State Pattern
            PatientState state = new InTreatmentState();
            state.nextState(patient); // IN_TREATMENT -> DISCHARGED
            
            Doctor doctor = patient.getAssignedDoctor();
            if (doctor != null) {
                int fatigue = 10;
                if ("RED".equalsIgnoreCase(patient.getUrgencyLevel())) fatigue = 20;
                
                doctor.setEnergyLevel(Math.max(0, doctor.getEnergyLevel() - fatigue));
                doctor.setAvailable(true);
                doctor.setPatientsTreated(doctor.getPatientsTreated() + 1);
                
                if (doctor.getEnergyLevel() < 10) {
                    doctor.setResting(true);
                }

                doctorRepository.save(doctor);
            }
            patient.setAssignedDoctor(null);
            patientRepository.save(patient);
            
            assignDoctorToNextPatient();
        }
    }
    
    public void assignDoctorToNextPatient() {
        List<Patient> quarantinedPatients = patientRepository.findByCurrentStateStr("QUARANTINED");
        List<Patient> waitingPatients = patientRepository.findByCurrentStateStr("WAITING");
        
        if (quarantinedPatients.isEmpty() && waitingPatients.isEmpty()) return;
        
        Patient nextPatient;
        if (!quarantinedPatients.isEmpty()) {
            // 2. Strategy Pattern
            List<Patient> sortedQ = priorityTriageStrategy.sortPatients(quarantinedPatients);
            nextPatient = sortedQ.get(0);
        } else {
            // 2. Strategy Pattern
            List<Patient> sortedW = priorityTriageStrategy.sortPatients(waitingPatients);
            nextPatient = sortedW.get(0);
        }
        
        assignDoctorToPatient(nextPatient);
    }

    public List<Patient> getWaitingPatients() {
        List<Patient> waiting = patientRepository.findByCurrentStateStr("WAITING");
        // 2. Strategy Pattern
        return priorityTriageStrategy.sortPatients(waiting);
    }
    
    public List<Patient> getInTreatmentPatients() {
        return patientRepository.findByCurrentStateStr("IN_TREATMENT");
    }

    public List<Patient> getQuarantinedPatients() {
        List<Patient> quarantined = patientRepository.findByCurrentStateStr("QUARANTINED");
        return priorityTriageStrategy.sortPatients(quarantined);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    
    public void triggerEpidemic(int count) {
        if (!simulationManager.isSimulationRunning()) {
            throw new RuntimeException("Simulation is paused!");
        }

        for (int i = 0; i < count; i++) {
            Patient newPatient = new Patient("Salgın Vakası " + (i + 1), 30 + (int)(Math.random() * 40), "Grip (Salgın Şüphesi)", "RED");
            newPatient.setCurrentStateStr("QUARANTINED");
            patientRepository.save(newPatient);
            
            // 4. Observer Pattern
            emergencyNotifier.notifyObservers(newPatient);
            assignDoctorToPatient(newPatient);
        }
    }

    private boolean isInfectious(String complaint) {
        if (complaint == null) return false;
        String lower = complaint.toLowerCase();
        return lower.contains("ateş") || lower.contains("grip") || 
               lower.contains("öksürük") || lower.contains("enfeksiyon") || 
               lower.contains("salgın") || lower.contains("covid");
    }

    public List<Patient> getDischargedPatients() {
        return patientRepository.findByCurrentStateStr("DISCHARGED");
    }
}
