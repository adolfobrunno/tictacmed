package com.abba.tictacmed.domain.patient.repository;

import com.abba.tictacmed.domain.patient.model.Patient;

import java.util.Optional;

public interface PatientRepository {
    Patient save(Patient patient);

    Optional<Patient> findById(String id);

    Optional<Patient> findByContact(String contact);
}
