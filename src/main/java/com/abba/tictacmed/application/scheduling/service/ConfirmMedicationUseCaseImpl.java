package com.abba.tictacmed.application.scheduling.service;

import com.abba.tictacmed.application.scheduling.command.ConfirmMedicationCommand;
import com.abba.tictacmed.application.scheduling.usecases.ConfirmMedicationUseCase;
import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.patient.repository.PatientRepository;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;
import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Objects;

@Service
public class ConfirmMedicationUseCaseImpl implements ConfirmMedicationUseCase {

    private final MedicationScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;

    public ConfirmMedicationUseCaseImpl(MedicationScheduleRepository scheduleRepository,
                                        PatientRepository patientRepository) {
        this.scheduleRepository = Objects.requireNonNull(scheduleRepository, "scheduleRepository");
        this.patientRepository = Objects.requireNonNull(patientRepository, "patientRepository");
    }

    @Override
    @Transactional
    public void execute(ConfirmMedicationCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        if (cmd.patientContact() == null || cmd.patientContact().isBlank()) {
            throw new IllegalArgumentException("patient contact is required");
        }
        if (cmd.medicineName() == null || cmd.medicineName().isBlank()) {
            throw new IllegalArgumentException("medicine name is required");
        }

        Patient patient = patientRepository.findByContact(cmd.patientContact())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + cmd.patientContact()));

        // Load schedules for patient and find the next scheduled record for the given medicine
        var schedules = scheduleRepository.findByPatientId(patient.getContact());
        MedicationSchedule targetSchedule = schedules.stream()
                .filter(s -> s.getMedicineName().equalsIgnoreCase(cmd.medicineName()))
                .min(Comparator.comparing(MedicationSchedule::getStartAt))
                .orElseThrow(() -> new IllegalArgumentException("No schedule found for medicine: " + cmd.medicineName()));

        // Find next scheduled admin for this schedule (status SCHEDULED)
        var nextDueOpt = targetSchedule.nextDue(OffsetDateTime.now());
        if (nextDueOpt.isEmpty()) {
            // Nothing scheduled/upcoming; nothing to confirm/skip
            return;
        }
        var scheduledAt = nextDueOpt.get();

        // Update the record status using domain methods
        if (cmd.confirmed()) {
            targetSchedule.confirmAdministration(scheduledAt, OffsetDateTime.now());
        } else {
            targetSchedule.skipAdministration(scheduledAt);
        }

        scheduleRepository.save(targetSchedule);
    }
}
