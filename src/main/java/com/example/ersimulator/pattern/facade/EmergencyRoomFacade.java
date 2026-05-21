package com.example.ersimulator.pattern.facade;

import com.example.ersimulator.model.Doctor;
import com.example.ersimulator.model.Patient;


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

// ╔══════════════════════════════════════════════════════════════════╗
// ║  FACADE DESENİ (Cephe Deseni)                                   ║
// ║  Bu sınıf projenin BEYNİDİR.                                    ║
// ║  5 alt sistemi (PatientRepo, DoctorRepo, Inventory, Strategy,   ║
// ║  SimulationManager) tek bir merkezden koordine eder.             ║
// ║  Client'ları: EmergencyController ve GameLoopScheduler           ║
// ║  Dışarıya basit metodlar sunar, arkadaki karmaşıklığı gizler.   ║
// ╚══════════════════════════════════════════════════════════════════╝
@Service // Spring bu sınıfı Singleton olarak yönetir (tüm uygulamada 1 tane)
public class EmergencyRoomFacade {

    // ─── FACADE: Alt Sistem 1 — Hasta Veritabanı Erişimi ───
    @Autowired // Dependency Injection: Spring bu nesneyi otomatik enjekte eder
    private PatientRepository patientRepository;

    // ─── FACADE: Alt Sistem 2 — Doktor Veritabanı Erişimi ───
    @Autowired
    private DoctorRepository doctorRepository;

    // ─── FACADE: Alt Sistem 3 — Strategy Deseni (Triyaj Sıralama) ───
    @Autowired
    private PriorityTriageStrategy priorityTriageStrategy;

    // ─── FACADE: Alt Sistem 4 — Tıbbi Stok/Envanter Yönetimi ───
    @Autowired
    private MedicalInventoryService inventoryService;

    // ─── FACADE: Alt Sistem 5 — Simülasyon Durum Yönetimi ───
    @Autowired
    private SimulationManager simulationManager;

    // ╔══════════════════════════════════════════════════════════════╗
    // ║  HASTA KABUL METODU                                         ║
    // ║  Bu tek metod arkada 6 farklı adım çalıştırır.              ║
    // ║  Controller sadece facade.admitPatient() çağırır,            ║
    // ║  arkadaki karmaşıklığı bilmez — FACADE'ın gücü budur.       ║
    // ║  Kullanan Desenler: STATE                                    ║
    // ╚══════════════════════════════════════════════════════════════╝
    public Patient admitPatient(String name, int age, String complaint, String urgencyLevel) {
        // ADIM 1: Simülasyon çalışıyor mu kontrol et
        if (!simulationManager.isSimulationRunning()) {
            throw new RuntimeException("Simulation is paused!");
        }

        // ADIM 2: Yeni hasta nesnesi oluştur (Constructor ile)
        Patient newPatient = new Patient(name, age, complaint, urgencyLevel);
        
        // ─── STATE DESENİ: Hastanın başlangıç durumunu belirle ───
        // Salgın modu açıksa VE şikayet bulaşıcıysa → QUARANTINED (Karantina)
        // Değilse → WAITING (Bekleme)
        if (simulationManager.isQuarantineMode() && isInfectious(complaint)) {
            newPatient.setCurrentStateStr("QUARANTINED"); // State: Karantina durumuna koy
            System.out.println("🚨 Salgın Modu: " + newPatient.getName() + " karantinaya alındı.");
        } else {
            newPatient.setCurrentStateStr("WAITING"); // State: Bekleme durumuna koy
        }
        
        // ADIM 3: Hastayı veritabanına kaydet
        patientRepository.save(newPatient);

        // ADIM 4: Kırmızı kodda konsola bildirim yap
        if ("RED".equalsIgnoreCase(urgencyLevel)) {
            System.out.println("🚨 ACİL DURUM: " + newPatient.getName() + " için Kırmızı Kod!");
        }
        
        // ADIM 5: Müsait doktor varsa hastaya doktor ata
        assignDoctorToPatient(newPatient);

        return newPatient;
    }

    // ╔══════════════════════════════════════════════════════════════╗
    // ║  DOKTOR ATAMA METODU                                        ║
    // ║  Bekleyen veya karantinadaki bir hastaya müsait doktor atar. ║
    // ║  Kullanan Desenler: STATE + FACADE (envanter kontrolü)       ║
    // ╚══════════════════════════════════════════════════════════════╝
    public void assignDoctorToPatient(Patient patient) {
        // Sadece WAITING veya QUARANTINED durumdaki hastalara doktor atanabilir
        if ("WAITING".equals(patient.getCurrentStateStr()) || "QUARANTINED".equals(patient.getCurrentStateStr())) {
            
            // FACADE: Alt Sistem 2 — Müsait doktorları veritabanından çek
            List<Doctor> availableDoctors = doctorRepository.findByIsAvailable(true);
            
            if (!availableDoctors.isEmpty()) {
                
                // FACADE: Alt Sistem 4 — Stok yeterli mi kontrol et
                boolean hasSupplies = requestSuppliesForTreatment(patient.getUrgencyLevel());
                if (!hasSupplies) {
                    System.out.println("STOK YETERSİZ! " + patient.getName() + " için atama yapılamadı.");
                    return; // Stok yoksa atama yapma
                }

                Doctor assigned = availableDoctors.get(0); // İlk müsait doktoru al
                
                // Dinlenen doktora atama yapma
                if(assigned.isResting()) {
                    System.out.println(assigned.getName() + " dinleniyor, atama yapılamaz.");
                    return;
                }

                assigned.setAvailable(false); // Doktoru meşgul yap
                patient.setAssignedDoctor(assigned); // Hastaya doktoru ata

                // Tedavi süresini ayarla (Patient constructor'da belirlenmişti)
                int finalTime = patient.getTreatmentTimeRemaining();
                patient.setTreatmentTimeRemaining(finalTime);

                // ╔════════════════════════════════════════════╗
                // ║  ★ STATE DESENİ: Durum Geçişi ★            ║
                // ║  Hastanın durumuna göre doğru State         ║
                // ║  nesnesini oluştur ve nextState() çağır.    ║
                // ║  WAITING → IN_TREATMENT                    ║
                // ║  QUARANTINED → IN_TREATMENT                ║
                // ║  Client: Bu metod (Facade)                 ║
                // ║  Context: Patient nesnesi                  ║
                // ╚════════════════════════════════════════════╝
                PatientState state;
                if ("QUARANTINED".equals(patient.getCurrentStateStr())) {
                    state = new QuarantinedState(); // Concrete State: Karantina
                } else {
                    state = new WaitingState(); // Concrete State: Bekleme
                }
                state.nextState(patient); // Polimorfizm: Hangi state ise onun nextState'i çalışır
                // → Sonuç: patient.currentStateStr artık "IN_TREATMENT"
                
                // Veritabanına güncelle
                doctorRepository.save(assigned);
                patientRepository.save(patient);
            }
        }
    }

    // ─── FACADE: Alt Sistem 4 — Envanter stok kontrolü ve tüketimi ───
    // Aciliyete göre farklı malzeme gereksinimi var
    private boolean requestSuppliesForTreatment(String urgencyLevel) {
        if ("RED".equalsIgnoreCase(urgencyLevel)) {
            if (inventoryService.hasStockForRed()) {
                inventoryService.consumeForRed(); // Kan, serum, şırınga, antibiyotik düş
                return true;
            }
        } else if ("YELLOW".equalsIgnoreCase(urgencyLevel)) {
            if (inventoryService.hasStockForYellow()) {
                inventoryService.consumeForYellow(); // Serum, ağrı kesici, şırınga düş
                return true;
            }
        } else if ("GREEN".equalsIgnoreCase(urgencyLevel)) {
            if (inventoryService.hasStockForGreen()) {
                inventoryService.consumeForGreen(); // Bandaj veya aşı düş
                return true;
            }
        }
        return false; // Stok yetersiz
    }

    // ╔══════════════════════════════════════════════════════════════╗
    // ║  TABURCU ETME METODU                                        ║
    // ║  Bu metod 2 deseni aynı anda kullanır:                      ║
    // ║  1. DECORATOR → Fatura oluşturma (iç içe sarma)             ║
    // ║  2. STATE → IN_TREATMENT → DISCHARGED geçişi                ║
    // ║  GameLoop süre bittiğinde bu metodu otomatik çağırır.        ║
    // ╚══════════════════════════════════════════════════════════════╝
    public void dischargePatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId).orElseThrow();
        
        // Sadece tedavide olan hasta taburcu edilebilir
        if ("IN_TREATMENT".equals(patient.getCurrentStateStr())) {
            
            // ╔════════════════════════════════════════════════════════╗
            // ║  ★ DECORATOR DESENİ: Fatura Oluşturma ★               ║
            // ║  BasicTreatment (100₺) çekirdeğinin üzerine            ║
            // ║  katman katman malzeme sararak fatura büyütülür.        ║
            // ║  Her decorator, içindekinin fiyatına kendi fiyatını     ║
            // ║  ekler — iç içe sarma (wrapping) mekanizması.          ║
            // ║  Client: Bu metod (Facade)                             ║
            // ╚════════════════════════════════════════════════════════╝
            
            // Katman 1: Temel muayene (çekirdek) — 100₺
            com.example.ersimulator.pattern.decorator.Treatment treatment = new com.example.ersimulator.pattern.decorator.BasicTreatment();
            
            // Katman 2: Her hastaya yatak ücreti eklenir — +500₺
            treatment = new com.example.ersimulator.pattern.decorator.BedFeeDecorator(treatment);
            
            String complaint = patient.getComplaint().toLowerCase();

            // Katman 3: Aciliyete göre ek malzeme sarılır
            if ("RED".equalsIgnoreCase(patient.getUrgencyLevel())) {
                // Kırmızı Kod: Kan Nakli (+1500₺) + Serum (+250₺)
                treatment = new com.example.ersimulator.pattern.decorator.SerumDecorator(
                              new com.example.ersimulator.pattern.decorator.BloodUnitDecorator(treatment));
            } else if ("YELLOW".equalsIgnoreCase(patient.getUrgencyLevel())) {
                // Sarı Kod: Ağrı Kesici (+50₺) + Serum (+250₺)
                treatment = new com.example.ersimulator.pattern.decorator.SerumDecorator(
                              new com.example.ersimulator.pattern.decorator.PainkillerDecorator(treatment));
            }

            // Katman 4: Şikayete göre ek ilaç sarılır
            if (complaint.contains("ateş") || complaint.contains("enfeksiyon")) {
                treatment = new com.example.ersimulator.pattern.decorator.AntibioticDecorator(treatment); // +120₺
            } else if (complaint.contains("ağrı")) {
                treatment = new com.example.ersimulator.pattern.decorator.PainkillerDecorator(treatment); // +50₺
            } else if (complaint.contains("ısırık") || complaint.contains("kesik")) {
                treatment = new com.example.ersimulator.pattern.decorator.VaccineDecorator(treatment); // +85₺
            }

            // Tüm katmanların toplam açıklaması ve fiyatı hastaya kaydedilir
            patient.setFinalBill(String.format("Malzemeler: %s | Fatura: %.2f TL", treatment.getDescription(), treatment.getCost()));

            // ╔════════════════════════════════════════════╗
            // ║  ★ STATE DESENİ: Durum Geçişi ★            ║
            // ║  IN_TREATMENT → DISCHARGED                 ║
            // ║  Tedavisi biten hasta taburcu edilir.       ║
            // ╚════════════════════════════════════════════╝
            PatientState state = new InTreatmentState(); // Concrete State: Tedavide
            state.nextState(patient); // → patient.currentStateStr = "DISCHARGED"
            
            // Doktoru serbest bırak ve yorgunluğunu güncelle
            Doctor doctor = patient.getAssignedDoctor();
            if (doctor != null) {
                int fatigue = 10; // Normal yorgunluk
                if ("RED".equalsIgnoreCase(patient.getUrgencyLevel())) fatigue = 20; // Kırmızı kod daha yorucu
                
                doctor.setEnergyLevel(Math.max(0, doctor.getEnergyLevel() - fatigue));
                doctor.setAvailable(true); // Doktor artık müsait
                doctor.setPatientsTreated(doctor.getPatientsTreated() + 1); // Baktığı hasta sayısını artır
                
                // Enerji %10'un altına düştüyse dinlenmeye gönder
                if (doctor.getEnergyLevel() < 10) {
                    doctor.setResting(true);
                }

                doctorRepository.save(doctor);
            }
            patient.setAssignedDoctor(null); // Hasta-doktor bağlantısını kopar
            patientRepository.save(patient);
            
            // Taburcu sonrası sıradaki hastaya doktor ata
            assignDoctorToNextPatient();
        }
    }
    
    // ╔══════════════════════════════════════════════════════════════╗
    // ║  SIRADAKİ HASTAYI SEÇ VE DOKTOR ATA                        ║
    // ║  Kullanan Desen: STRATEGY (sıralama algoritması)             ║
    // ║  Önce karantina hastaları kontrol edilir (öncelikli),        ║
    // ║  sonra normal bekleyenler.                                   ║
    // ╚══════════════════════════════════════════════════════════════╝
    public void assignDoctorToNextPatient() {
        List<Patient> quarantinedPatients = patientRepository.findByCurrentStateStr("QUARANTINED");
        List<Patient> waitingPatients = patientRepository.findByCurrentStateStr("WAITING");
        
        // Hiç bekleyen yoksa çık
        if (quarantinedPatients.isEmpty() && waitingPatients.isEmpty()) return;
        
        Patient nextPatient;
        if (!quarantinedPatients.isEmpty()) {
            // ★ STRATEGY DESENİ: Karantina hastalarını aciliyete göre sırala ★
            List<Patient> sortedQ = priorityTriageStrategy.sortPatients(quarantinedPatients);
            nextPatient = sortedQ.get(0); // En acil karantina hastasını al
        } else {
            // ★ STRATEGY DESENİ: Bekleyen hastaları aciliyete göre sırala ★
            List<Patient> sortedW = priorityTriageStrategy.sortPatients(waitingPatients);
            nextPatient = sortedW.get(0); // En acil bekleyen hastayı al
        }
        
        assignDoctorToPatient(nextPatient);
    }

    // ─── FACADE: Bekleme listesini Strategy ile sıralayıp döndür ───
    public List<Patient> getWaitingPatients() {
        List<Patient> waiting = patientRepository.findByCurrentStateStr("WAITING");
        // ★ STRATEGY DESENİ: Listeyi aciliyete göre sırala ★
        return priorityTriageStrategy.sortPatients(waiting);
    }
    
    // ─── FACADE: Tedavideki hastaları döndür ───
    public List<Patient> getInTreatmentPatients() {
        return patientRepository.findByCurrentStateStr("IN_TREATMENT");
    }

    // ─── FACADE: Karantina listesini Strategy ile sıralayıp döndür ───
    public List<Patient> getQuarantinedPatients() {
        List<Patient> quarantined = patientRepository.findByCurrentStateStr("QUARANTINED");
        // ★ STRATEGY DESENİ: Karantina listesini de aciliyete göre sırala ★
        return priorityTriageStrategy.sortPatients(quarantined);
    }

    // ─── FACADE: Tüm doktorları döndür ───
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    
    // ╔══════════════════════════════════════════════════════════════╗
    // ║  SALGIN (EPİDEMİC) TETİKLEME                                ║
    // ║  Kullanan Desenler: STATE + STRATEGY + FACADE                ║
    // ║  10 hasta oluşturup hepsini QUARANTINED durumuna koyar.      ║
    // ╚══════════════════════════════════════════════════════════════╝
    public void triggerEpidemic(int count) {
        if (!simulationManager.isSimulationRunning()) {
            throw new RuntimeException("Simulation is paused!");
        }

        for (int i = 0; i < count; i++) {
            // Salgın hastası oluştur
            Patient newPatient = new Patient("Salgın Vakası " + (i + 1), 30 + (int)(Math.random() * 40), "Grip (Salgın Şüphesi)", "RED");
            
            // ★ STATE DESENİ: Doğrudan QUARANTINED durumuna koy ★
            newPatient.setCurrentStateStr("QUARANTINED");
            patientRepository.save(newPatient);
            
            System.out.println("🚨 SALGIN ALARMI: " + newPatient.getName() + " tespit edildi!");
            
            // Müsait doktor varsa ata (içinde State + Strategy çalışır)
            assignDoctorToPatient(newPatient);
        }
    }

    // ─── Şikayetin bulaşıcı olup olmadığını kontrol eden yardımcı metod ───
    private boolean isInfectious(String complaint) {
        if (complaint == null) return false;
        String lower = complaint.toLowerCase();
        return lower.contains("ateş") || lower.contains("grip") || 
               lower.contains("öksürük") || lower.contains("enfeksiyon") || 
               lower.contains("salgın") || lower.contains("covid");
    }

    // ─── FACADE: Taburcu edilmiş hastaları döndür ───
    public List<Patient> getDischargedPatients() {
        return patientRepository.findByCurrentStateStr("DISCHARGED");
    }
}
