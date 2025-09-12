package com.abba.tictacmed.domain.patient.repository;

import com.abba.tictacmed.domain.patient.model.Patient;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository {
    Patient save(Patient patient);
    Optional<Patient> findById(UUID id);

    Optional<Patient> findByContact(String contact);
}
