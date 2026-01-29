package com.abba.tanahora.application.service;

import com.abba.tanahora.domain.model.Medication;
import com.abba.tanahora.domain.model.User;
import com.abba.tanahora.domain.repository.MedicationRepository;
import com.abba.tanahora.domain.service.MedicationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MedicationServiceImpl implements MedicationService {

    private final MedicationRepository medicationRepository;

    public MedicationServiceImpl(MedicationRepository medicationRepository) {
        this.medicationRepository = medicationRepository;
    }

    @Override
    public Medication createMedication(User user, String name) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        Medication medication = new Medication();
        medication.setUser(user);
        medication.setName(name);
        return medicationRepository.save(medication);
    }

    @Override
    public List<Medication> listByUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        return medicationRepository.findByUser(user);
    }

    @Override
    public Optional<Medication> getById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return medicationRepository.findById(id);
    }
}
