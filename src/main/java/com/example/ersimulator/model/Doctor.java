package com.example.ersimulator.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  DOKTOR MODELİ                                                  ║
// ║  JPA @Entity: Bu sınıf "doctors" tablosuna eşlenir.             ║
// ║  isAvailable: Doktor müsait mi? (Facade hasta atarken kontrol eder) ║
// ║  isResting: Doktor dinleniyor mu? (GameLoop enerji doldurur)    ║
// ║  energyLevel: Enerji seviyesi (%0-100). %10 altında dinlenmeye girer. ║
// ║  GameLoop her saniye dinlenen doktorun enerjisini %5 artırır.   ║
// ╚══════════════════════════════════════════════════════════════════╝
@Entity
@Data
@NoArgsConstructor
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;           // Doktor adı
    private String specialization; // Uzmanlık alanı (Surgeon, Cardiologist vb.)
    
    // ─── Yorgunluk Sistemi ───
    private int energyLevel = 100;      // Başlangıç: %100 enerjili
    private int patientsTreated = 0;    // Toplam baktığı hasta sayısı
    private boolean isResting = false;  // Dinleniyor mu? (GameLoop kontrol eder)

    // ─── Müsaitlik Durumu ───
    // Hasta atandığında false olur, taburcu edildiğinde true olur
    private boolean isAvailable = true;

    public Doctor(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
    }
}
