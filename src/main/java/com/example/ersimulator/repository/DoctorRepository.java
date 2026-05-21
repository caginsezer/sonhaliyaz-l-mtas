package com.example.ersimulator.repository;

import com.example.ersimulator.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  DOKTOR VERİTABANI ERİŞİM KATMANI (DoctorRepository)             ║
// ║  Spring Data JPA'nın REPOSITORY desenini (Pattern) uygular.       ║
// ║  SQL sorguları yazmadan veritabanı işlemlerini halleder.         ║
// ║  FACADE deseninin ALT SİSTEMİDİR (Subsystem).                   ║
// ║  JpaRepository'yi extend ederek save(), findById() gibi CRUD     ║
// ║  metodlarını otomatik kazanır.                                  ║
// ╚══════════════════════════════════════════════════════════════════╝
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    
    // ─── DİNAMİK SQL METODU ───
    // Spring bu metodun isminden otomatik olarak SQL sorgusu türetir:
    // SELECT * FROM doctors WHERE is_available = :isAvailable
    // Facade, bekleme sırasındaki hastalara müsait doktorları atamak için bu sorguyu kullanır.
    List<Doctor> findByIsAvailable(boolean isAvailable);
}
