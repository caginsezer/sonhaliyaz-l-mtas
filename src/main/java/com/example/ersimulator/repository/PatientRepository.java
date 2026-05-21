package com.example.ersimulator.repository;

import com.example.ersimulator.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// ╔══════════════════════════════════════════════════════════════════╗
// ║  HASTA VERİTABANI ERİŞİM KATMANI (PatientRepository)             ║
// ║  Spring Data JPA'nın REPOSITORY desenini (Pattern) uygular.       ║
// ║  SQL sorguları yazmadan veritabanı işlemlerini halleder.         ║
// ║  FACADE deseninin ALT SİSTEMİDİR (Subsystem).                   ║
// ║  JpaRepository'yi extend ederek save(), findById() gibi CRUD     ║
// ║  metodlarını otomatik kazanır.                                  ║
// ╚══════════════════════════════════════════════════════════════════╝
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    // ─── DİNAMİK SQL METODU ───
    // Spring bu metodun isminden otomatik olarak SQL sorgusu türetir:
    // SELECT * FROM patients WHERE current_state_str = :currentStateStr
    // Bu sayede STATE desenindeki durum filtrelemelerini (WAITING, IN_TREATMENT vb.) hızlıca yapabiliriz.
    List<Patient> findByCurrentStateStr(String currentStateStr);
}
