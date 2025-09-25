package com.abba.tictacmed.application.scheduling.service;

import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleCommand;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleResult;
import com.abba.tictacmed.application.scheduling.usecases.CreateMedicationScheduleUseCase;
import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.patient.repository.PatientRepository;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;
import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Objects;

@Service
public class CreateMedicationScheduleUseCaseImpl implements CreateMedicationScheduleUseCase {

    private final PatientRepository patientRepository;
    private final MedicationScheduleRepository scheduleRepository;

    public CreateMedicationScheduleUseCaseImpl(PatientRepository patientRepository, MedicationScheduleRepository scheduleRepository) {
        this.patientRepository = Objects.requireNonNull(patientRepository);
        this.scheduleRepository = Objects.requireNonNull(scheduleRepository);
    }

    @Transactional
    @Override
    public CreateMedicationScheduleResult execute(CreateMedicationScheduleCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        String patientContact = Objects.requireNonNull(cmd.patientContact(), "patientContact");
        Patient patient = patientRepository.findByContact(patientContact)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + patientContact));

        MedicationSchedule schedule = MedicationSchedule.create(
                patient,
                Objects.requireNonNull(cmd.medicineName(), "medicineName"),
                Objects.requireNonNull(cmd.startAt(), "startAt"),
                Objects.requireNonNull(cmd.endAt(), "endAt"),
                Duration.ofSeconds(cmd.frequency().getSeconds())
        );

        schedule = scheduleRepository.save(schedule);

        return new CreateMedicationScheduleResult(
                schedule.getId(),
                schedule.getPatient().getId(),
                schedule.getMedicineName(),
                schedule.getStartAt(),
                schedule.getEndAt(),
                schedule.getFrequency()
        );
    }
}
