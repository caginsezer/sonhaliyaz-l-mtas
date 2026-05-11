package com.example.ersimulator.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private String complaint;

    // Kırmızı, Sarı, Yeşil
    private String urgencyLevel; 

    // WAITING, IN_TREATMENT, DISCHARGED
    private String currentStateStr = "WAITING"; 
    
    // Game Loop için eklendi: Kaç "tick" (saniye) boyunca tedavi göreceği
    private int treatmentTimeRemaining = 0;

    // Faz 7: Hastanın nihai raporunu/faturasını tutmak için
    private String finalBill;

    private LocalDateTime arrivalTime;
    
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor assignedDoctor;

    public Patient(String name, int age, String complaint, String urgencyLevel) {
        this.name = name;
        this.age = age;
        this.complaint = complaint;
        this.urgencyLevel = urgencyLevel != null ? urgencyLevel.toUpperCase() : null;
        this.arrivalTime = LocalDateTime.now();

        if (this.urgencyLevel == null) {
            return;
        }

        switch (this.urgencyLevel) {
            case "RED":
                this.treatmentTimeRemaining = 1800; 
                break;
            case "YELLOW":
                this.treatmentTimeRemaining = 900;
                break;
            case "GREEN":
                this.treatmentTimeRemaining = 300;
                break;
            default:
                throw new IllegalArgumentException("Unknown Patient Type: " + urgencyLevel);
        }
    }
}
