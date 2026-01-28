package com.abba.tictacmed.domain.service;

import com.abba.tictacmed.domain.model.Medication;
import com.abba.tictacmed.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicationService {

    Medication createMedication(User user, String name);

    List<Medication> listByUser(User user);

    Optional<Medication> getById(UUID id);
}
