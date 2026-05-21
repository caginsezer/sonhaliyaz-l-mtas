package com.example.ersimulator.controller;

import com.example.ersimulator.model.Doctor;
import com.example.ersimulator.model.Patient;
import com.example.ersimulator.pattern.facade.EmergencyRoomFacade;
import com.example.ersimulator.service.MedicalInventoryService;
import com.example.ersimulator.service.SimulationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  REST CONTROLLER — FACADE DESENİNİN CLIENT'I (İSTEMCİSİ)       ║
// ║  Frontend'den gelen HTTP isteklerini karşılar.                  ║
// ║  İş mantığını BİLMEZ — sadece Facade'ı çağırır.                ║
// ║  Bu sınıfta PatientRepository, DoctorRepository gibi alt        ║
// ║  sistemler YOKTUR — her şey Facade üzerinden geçer.             ║
// ║  Facade olmasaydı burada 200+ satır iş mantığı olurdu.         ║
// ╚══════════════════════════════════════════════════════════════════╝
@RestController
@RequestMapping("/api/emergency")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE}) 
public class EmergencyController {

    // ─── FACADE: Controller sadece Facade'ı tanır ───
    @Autowired
    private EmergencyRoomFacade emergencyRoomFacade;

    @Autowired
    private MedicalInventoryService inventoryService;

    @Autowired
    private SimulationManager simulationManager;

    @Autowired
    private com.example.ersimulator.repository.DoctorRepository doctorRepository;

    // ─── Hasta Kabul: Facade.admitPatient() çağırır ───
    // İçinde STATE + STRATEGY + FACADE desenleri çalışır
    @PostMapping("/admit")
    public ResponseEntity<Patient> admitPatient(@RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        int age = Integer.parseInt(payload.get("age"));
        String complaint = payload.get("complaint");
        String urgencyLevel = payload.get("urgencyLevel");

        // Tek satır — arkada 6 adım çalışır ama Controller bilmez
        Patient patient = emergencyRoomFacade.admitPatient(name, age, complaint, urgencyLevel);
        return ResponseEntity.ok(patient);
    }

    // ─── Bekleyen Hastaları Getir: STRATEGY ile sıralı döner ───
    @GetMapping("/patients/waiting")
    public ResponseEntity<List<Patient>> getWaitingPatients() {
        return ResponseEntity.ok(emergencyRoomFacade.getWaitingPatients());
    }

    // ─── Karantina Hastalarını Getir: STRATEGY ile sıralı döner ───
    @GetMapping("/patients/quarantined")
    public ResponseEntity<List<Patient>> getQuarantinedPatients() {
        return ResponseEntity.ok(emergencyRoomFacade.getQuarantinedPatients());
    }

    // ─── Tedavideki Hastaları Getir: STATE = IN_TREATMENT olanlar ───
    @GetMapping("/patients/treating")
    public ResponseEntity<List<Patient>> getInTreatmentPatients() {
        return ResponseEntity.ok(emergencyRoomFacade.getInTreatmentPatients());
    }

    // ─── Taburcu Edilenleri Getir: STATE = DISCHARGED olanlar ───
    @GetMapping("/patients/discharged")
    public ResponseEntity<List<Patient>> getDischargedPatients() {
        return ResponseEntity.ok(emergencyRoomFacade.getDischargedPatients());
    }

    // ─── Tüm Doktorları Getir ───
    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(emergencyRoomFacade.getAllDoctors());
    }

    // ─── Hasta Taburcu Et: DECORATOR (fatura) + STATE (geçiş) çalışır ───
    @PostMapping("/discharge/{patientId}")
    public ResponseEntity<Patient> dischargePatient(@PathVariable Long patientId) {
        // Taburcu öncesi hastayı bulalım ki faturasını görebilelim
        Patient patient = emergencyRoomFacade.getInTreatmentPatients().stream()
                .filter(p -> p.getId().equals(patientId))
                .findFirst()
                .orElse(null);

        emergencyRoomFacade.dischargePatient(patientId);
        
        return ResponseEntity.ok(patient);
    }

    // ─── Simülasyonu Başlat/Durdur ───
    @PostMapping("/simulation/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleSimulation() {
        simulationManager.setSimulationRunning(!simulationManager.isSimulationRunning());
        return ResponseEntity.ok(Map.of("isRunning", simulationManager.isSimulationRunning()));
    }

    @GetMapping("/simulation/status")
    public ResponseEntity<Map<String, Boolean>> getSimulationStatus() {
        return ResponseEntity.ok(Map.of("isRunning", simulationManager.isSimulationRunning()));
    }

    // ─── Tıbbi Envanter: FACADE'ın alt sistemi ───
    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Integer>> getInventory() {
        return ResponseEntity.ok(inventoryService.getInventoryStatus());
    }

    @PostMapping("/inventory/restock")
    public ResponseEntity<String> restockInventory() {
        inventoryService.restockAll();
        return ResponseEntity.ok("Stoklar başarıyla yenilendi.");
    }
    
    // ─── Doktor Dinlenme: GameLoop ile bağlantılı ───
    @PostMapping("/doctors/rest/{doctorId}")
    public ResponseEntity<Doctor> restDoctor(@PathVariable Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow();
        doctor.setResting(true);
        doctor.setEnergyLevel(Math.min(100, doctor.getEnergyLevel() + 30));
        if (doctor.getEnergyLevel() >= 100) {
            doctor.setResting(false);
        }
        doctorRepository.save(doctor);
        return ResponseEntity.ok(doctor);
    }

    // ─── Salgın Tetikle: STATE + STRATEGY + FACADE birlikte çalışır ───
    @PostMapping("/epidemic")
    public ResponseEntity<String> triggerEpidemic() {
        emergencyRoomFacade.triggerEpidemic(10);
        return ResponseEntity.ok("Salgın alarmı verildi! 10 hasta karantinaya alındı.");
    }

    // ─── Karantina Modunu Aç/Kapa ───
    @PostMapping("/quarantine/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleQuarantine() {
        simulationManager.setQuarantineMode(!simulationManager.isQuarantineMode());
        return ResponseEntity.ok(Map.of("isQuarantineMode", simulationManager.isQuarantineMode()));
    }

    @GetMapping("/quarantine/status")
    public ResponseEntity<Map<String, Boolean>> getQuarantineStatus() {
        return ResponseEntity.ok(Map.of("isQuarantineMode", simulationManager.isQuarantineMode()));
    }
}
