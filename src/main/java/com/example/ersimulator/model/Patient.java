package com.example.ersimulator.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  HASTA MODELİ — STATE DESENİNİN CONTEXT'İ                      ║
// ║  JPA @Entity: Bu sınıf "patients" tablosuna eşlenir.           ║
// ║  currentStateStr alanı hastanın mevcut durumunu taşır:          ║
// ║  WAITING | QUARANTINED | IN_TREATMENT | DISCHARGED              ║
// ║  State nesneleri bu alanı değiştirerek durum geçişi yapar.      ║
// ║  urgencyLevel alanı Strategy deseninin girdisidir (RED/YELLOW/GREEN) ║
// ║  finalBill alanı Decorator deseninin çıktısını saklar.           ║
// ╚══════════════════════════════════════════════════════════════════╝
@Entity
@Data // Lombok: getter, setter, toString otomatik oluşturur
@NoArgsConstructor // Lombok: Parametresiz constructor oluşturur (JPA gerektirir)
@Table(name = "patients")
public class Patient {
    @Id // Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Otomatik artan ID
    private Long id;

    private String name;
    private int age;
    private String complaint; // Şikayet (ör: "Ateş, baş ağrısı")

    // ─── STRATEGY DESENİNİN GİRDİSİ ───
    // Kırmızı(RED), Sarı(YELLOW), Yeşil(GREEN) — sıralama önceliğini belirler
    private String urgencyLevel; 

    // ─── STATE DESENİNİN CONTEXT ALANI ───
    // WAITING → IN_TREATMENT → DISCHARGED (veya QUARANTINED → IN_TREATMENT → DISCHARGED)
    // State nesneleri bu alanı değiştirerek durum geçişi yapar
    private String currentStateStr = "WAITING"; 
    
    // GameLoop her saniye bu değeri 1 azaltır. 0 olunca hasta taburcu edilir.
    private int treatmentTimeRemaining = 0;

    // ─── DECORATOR DESENİNİN ÇIKTISI ───
    // Taburcu sırasında Decorator'ların oluşturduğu fatura buraya kaydedilir
    // Örnek: "Malzemeler: Temel Muayene, Yatak Ücreti + Kan Nakli | Fatura: 2100.00 TL"
    private String finalBill;

    private LocalDateTime arrivalTime; // Geliş zamanı — Strategy'de FIFO sıralaması için kullanılır
    
    // ─── VERİTABANI İLİŞKİSİ ───
    // ManyToOne: Birçok hasta → Bir doktor
    // Foreign Key: doctor_id sütunu doctors tablosuna referans verir
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor assignedDoctor;

    // Constructor: Yeni hasta oluşturulurken çağrılır
    public Patient(String name, int age, String complaint, String urgencyLevel) {
        this.name = name;
        this.age = age;
        this.complaint = complaint;
        this.urgencyLevel = urgencyLevel != null ? urgencyLevel.toUpperCase() : null;
        this.arrivalTime = LocalDateTime.now(); // Geliş zamanını kaydet

        if (this.urgencyLevel == null) {
            return;
        }

        // Aciliyete göre tedavi süresi belirlenir (saniye cinsinden)
        // GameLoop her saniye bu değeri 1 azaltır
        switch (this.urgencyLevel) {
            case "RED":
                this.treatmentTimeRemaining = 1800;  // 30 dakika (saniye)
                break;
            case "YELLOW":
                this.treatmentTimeRemaining = 900;   // 15 dakika
                break;
            case "GREEN":
                this.treatmentTimeRemaining = 300;   // 5 dakika
                break;
            default:
                throw new IllegalArgumentException("Unknown Patient Type: " + urgencyLevel);
        }
    }
}
