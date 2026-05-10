package com.example.ersimulator.config;

import com.example.ersimulator.model.Doctor;
import com.example.ersimulator.repository.DoctorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(DoctorRepository doctorRepository) {
        return args -> {
            if (doctorRepository.count() == 0) {
                doctorRepository.save(new Doctor("Dr. Ayşe Yılmaz", "Surgeon"));
                doctorRepository.save(new Doctor("Dr. Kerem Demir", "Cardiologist"));
                doctorRepository.save(new Doctor("Dr. Zeynep Kaya", "GP"));
                doctorRepository.save(new Doctor("Dr. Ali Çelik", "Surgeon"));
                doctorRepository.save(new Doctor("Dr. Fatma Öztürk", "Infectious Disease"));
                doctorRepository.save(new Doctor("Dr. Mehmet Yıldız", "GP"));
                doctorRepository.save(new Doctor("Dr. Elif Şahin", "Neurologist"));
                doctorRepository.save(new Doctor("Dr. Burak Arslan", "Orthopedist"));
                doctorRepository.save(new Doctor("Dr. Ceren Doğan", "Pediatrician"));
                doctorRepository.save(new Doctor("Dr. Mustafa Kılıç", "Surgeon"));
                doctorRepository.save(new Doctor("Dr. Aslı Çetin", "Cardiologist"));
                doctorRepository.save(new Doctor("Dr. Hakan Polat", "GP"));
            }
        };
    }
}
