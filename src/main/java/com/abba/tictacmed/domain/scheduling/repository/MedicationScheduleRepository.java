package com.abba.tictacmed.domain.scheduling.repository;

import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicationScheduleRepository {
    MedicationSchedule save(MedicationSchedule schedule);

    Optional<MedicationSchedule> findById(UUID id);

    List<MedicationSchedule> findByPatientId(UUID patientId);

    List<MedicationSchedule> findAll();
}
