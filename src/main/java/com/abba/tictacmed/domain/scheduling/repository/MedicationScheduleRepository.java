package com.abba.tictacmed.domain.scheduling.repository;

import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MedicationScheduleRepository {
    MedicationSchedule save(MedicationSchedule schedule);

    List<MedicationSchedule> findByPatientId(String patientId);

    List<MedicationSchedule> findAll();

    Optional<MedicationSchedule> findById(UUID id);

    Optional<MedicationSchedule.AdministrationRecord> findNextScheduled(Patient patient);
}
