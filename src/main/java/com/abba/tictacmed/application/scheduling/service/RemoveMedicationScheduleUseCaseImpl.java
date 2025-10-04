package com.abba.tictacmed.application.scheduling.service;

import com.abba.tictacmed.application.scheduling.command.RemoveMedicationScheduleCommand;
import com.abba.tictacmed.application.scheduling.usecases.RemoveMedicationScheduleUseCase;
import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.patient.repository.PatientRepository;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;
import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RemoveMedicationScheduleUseCaseImpl implements RemoveMedicationScheduleUseCase {

    private final MedicationScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;

    public RemoveMedicationScheduleUseCaseImpl(MedicationScheduleRepository scheduleRepository,
                                               PatientRepository patientRepository) {
        this.scheduleRepository = Objects.requireNonNull(scheduleRepository, "scheduleRepository");
        this.patientRepository = Objects.requireNonNull(patientRepository, "patientRepository");
    }

    @Override
    @Transactional
    public void execute(RemoveMedicationScheduleCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        if (cmd.patientContact() == null || cmd.patientContact().isBlank()) {
            throw new IllegalArgumentException("patient contact is required");
        }
        if (cmd.medicineName() == null || cmd.medicineName().isBlank()) {
            throw new IllegalArgumentException("medicine name is required");
        }

        Patient patient = patientRepository.findByContact(cmd.patientContact())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + cmd.patientContact()));

        // Fetch all schedules for this patient and cancel those matching the medicine name
        var schedules = scheduleRepository.findByPatientId(patient.getContact());
        for (MedicationSchedule schedule : schedules) {
            if (schedule.getMedicineName().equalsIgnoreCase(cmd.medicineName()) && schedule.isActive()) {
                schedule.cancelPendingAdministrations();
                schedule.deactivate();
                scheduleRepository.save(schedule);
            }
        }
    }
}
