package com.abba.tanahora.domain.service;

import com.abba.tanahora.domain.model.Medication;
import com.abba.tanahora.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicationService {

    Medication createMedication(User user, String name);

    List<Medication> listByUser(User user);

    Optional<Medication> getById(UUID id);
}
