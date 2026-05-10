package com.example.ersimulator.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialization;
    
    // Fatigue System
    private int energyLevel = 100;
    private int patientsTreated = 0;
    private boolean isResting = false;

    // To track if doctor is available
    private boolean isAvailable = true;

    public Doctor(String name, String specialization) {
        this.name = name;
        this.specialization = specialization;
    }
}
