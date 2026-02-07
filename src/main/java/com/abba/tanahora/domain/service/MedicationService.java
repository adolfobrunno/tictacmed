package com.abba.tanahora.domain.service;

import com.abba.tanahora.domain.model.Medication;
import com.abba.tanahora.domain.model.PatientRef;
import com.abba.tanahora.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicationService {

    Medication createMedication(User user, PatientRef patient, String name, String dosage);

    List<Medication> listByUser(User user);

    Optional<Medication> getById(UUID id);
}
