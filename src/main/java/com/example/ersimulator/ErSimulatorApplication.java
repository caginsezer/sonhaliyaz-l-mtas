package com.example.ersimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  ER SİMÜLATÖRÜ ANA UYGULAMA SINIFI (ErSimulatorApplication)      ║
// ║  Spring Boot projemizin BAŞLANGIÇ (Giriş) noktasıdır.            ║
// ║  @SpringBootApplication: Otomatik konfigürasyon, Component Scan   ║
// ║  ve Spring konfigürasyon özelliklerini tek başına aktif eder.    ║
// ║  @EnableScheduling: Spring'in arka plan zamanlayıcısını açar.    ║
// ║  Bu sayede GameLoopScheduler (@Scheduled) aktif hale gelir.      ║
// ╚══════════════════════════════════════════════════════════════════╝
@SpringBootApplication
@EnableScheduling // GameLoop otonom saat mekanizmasını çalıştırmak için şarttır
public class ErSimulatorApplication {

	public static void main(String[] args) {
		// Uygulamayı ayağa kaldırır ve Spring Container'ı (IoC) başlatır
		SpringApplication.run(ErSimulatorApplication.class, args);
	}

}
