package com.example.ersimulator.config;

import com.example.ersimulator.model.Doctor;
import com.example.ersimulator.repository.DoctorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  VERİTABANI BAŞLANGIÇ YÜKLEYİCİSİ (DataInitializer)             ║
// ║  Uygulama ilk ayağa kalktığında otomatik çalışır.               ║
// ║  H2 veritabanı in-memory (RAM) olduğu için her seferinde boştur.  ║
// ║  Bu sınıf, simülasyonun çalışabilmesi için 12 uzman doktoru     ║
// ║  veritabanına otomatik olarak kaydeder.                        ║
// ╚══════════════════════════════════════════════════════════════════╝
@Configuration // Spring Boot'un başlangıç konfigürasyon sınıfı olduğunu belirtir
public class DataInitializer {

    @Bean // Spring konteynerine bu metodun çıktısını bir Bean olarak kaydettirir
    CommandLineRunner initDatabase(DoctorRepository doctorRepository) {
        return args -> {
            // Eğer veritabanında hiç doktor yoksa (ilk başlatma anı) 12 doktoru ekle
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
                
                System.out.println("🌱 H2 Veritabanı Başlatıldı: 12 Uzman Doktor Kadrosu Eklendi.");
            }
        };
    }
}
