package com.example.ersimulator.service;

import org.springframework.stereotype.Service;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  SİMÜLASYON YÖNETİCİSİ (SimulationManager)                       ║
// ║  Bu servis, tüm simülasyonun küresel ayarlarını tutar.          ║
// ║  FACADE deseninin ALT SİSTEMİDİR (Subsystem).                   ║
// ║  Tutulan Ayarlar:                                               ║
// ║  - isSimulationRunning: Simülasyon aktif mi? (Durdur/Başlat)    ║
// ║  - isQuarantineMode: Salgın/Karantina bölgesi aktif mi?         ║
// ║  Bu ayarlar GameLoop ve Facade admitPatient() tarafından        ║
// ║  sorgulanarak otonom akışın kurallarını çizer.                  ║
// ╚══════════════════════════════════════════════════════════════════╝
@Service // Spring bu sınıfı Singleton bir Bean olarak yönetir
public class SimulationManager {

    // Simülasyonun zaman akışı (GameLoop'un çalışıp çalışmayacağını belirler)
    private boolean isSimulationRunning = true;
    
    // Simülasyonun zaman hızı çarpanı (Gelecekteki hızlandırmalar için)
    private int simulationSpeedMultiplier = 1;
    
    // Salgın/Karantina Modu Aktif mi? 
    // True ise: Bulaşıcı şikayeti olan yeni hastalar WAITING yerine QUARANTINED durumuna düşer.
    private boolean isQuarantineMode = false;

    // ─── Gelişmiş Getter & Setter Metodları ───

    public boolean isSimulationRunning() {
        return isSimulationRunning;
    }

    public void setSimulationRunning(boolean simulationRunning) {
        isSimulationRunning = simulationRunning;
    }

    public int getSimulationSpeedMultiplier() {
        return simulationSpeedMultiplier;
    }

    public void setSimulationSpeedMultiplier(int simulationSpeedMultiplier) {
        this.simulationSpeedMultiplier = simulationSpeedMultiplier;
    }

    public boolean isQuarantineMode() {
        return isQuarantineMode;
    }

    public void setQuarantineMode(boolean quarantineMode) {
        isQuarantineMode = quarantineMode;
    }
}
