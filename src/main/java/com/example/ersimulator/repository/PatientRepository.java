package com.example.ersimulator.repository;

import com.example.ersimulator.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByCurrentStateStr(String currentStateStr);
}
