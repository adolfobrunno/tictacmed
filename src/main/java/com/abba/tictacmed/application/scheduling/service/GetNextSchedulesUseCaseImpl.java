package com.abba.tictacmed.application.scheduling.service;

import com.abba.tictacmed.application.scheduling.command.NextSchedulesResult;
import com.abba.tictacmed.application.scheduling.usecases.GetNextSchedulesUseCase;
import com.abba.tictacmed.domain.patient.repository.PatientRepository;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;
import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
public class GetNextSchedulesUseCaseImpl implements GetNextSchedulesUseCase {

    private final MedicationScheduleRepository scheduleRepository;
    private final PatientRepository patientRepository;

    public GetNextSchedulesUseCaseImpl(MedicationScheduleRepository scheduleRepository, PatientRepository patientRepository) {
        this.scheduleRepository = Objects.requireNonNull(scheduleRepository);
        this.patientRepository = patientRepository;
    }

    @Override
    public NextSchedulesResult execute(String patientId, OffsetDateTime from, OffsetDateTime to) {
        Objects.requireNonNull(patientId, "patientId");

        Optional<MedicationSchedule.AdministrationRecord> next = scheduleRepository.findNextScheduled(patientRepository.findById(patientId).orElseThrow());

        return next.map(n -> new NextSchedulesResult(n.medicineName(), n.scheduledAt())).orElse(null);


//
//        return scheduleRepository.findByPatientId(patientId).stream()
//                .map(s -> s.nextDue(start)
//                        .filter(next -> !next.isAfter(end))
//                        .map(next -> new NextSchedulesResult(
//                                s.getId(), s.getPatient().getId(), s.getMedicineName(), next
//                        ))
//                        .orElse(null))
//                .filter(Objects::nonNull)
//                .sorted(Comparator.comparing(NextSchedulesResult::nextAt))
//                .toList();
    }

}
