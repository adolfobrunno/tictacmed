package com.abba.tictacmed.application.scheduling.service;

import com.abba.tictacmed.application.scheduling.command.NextSchedulesResult;
import com.abba.tictacmed.application.scheduling.usecases.GetNextSchedulesUseCase;
import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class GetNextSchedulesUseCaseImpl implements GetNextSchedulesUseCase {

    private final MedicationScheduleRepository scheduleRepository;

    public GetNextSchedulesUseCaseImpl(MedicationScheduleRepository scheduleRepository) {
        this.scheduleRepository = Objects.requireNonNull(scheduleRepository);
    }

    @Override
    public List<NextSchedulesResult> execute(UUID patientId, OffsetDateTime from, OffsetDateTime to) {
        Objects.requireNonNull(patientId, "patientId");
        OffsetDateTime start = from == null ? OffsetDateTime.now() : from;
        OffsetDateTime end = to == null ? start.plusDays(1) : to;
        if (!end.isAfter(start)) throw new IllegalArgumentException("to must be after from");

        return scheduleRepository.findByPatientId(patientId).stream()
                .map(s -> s.nextDue(start)
                        .filter(next -> !next.isAfter(end))
                        .map(next -> new NextSchedulesResult(
                                s.getId(), s.getPatient().getId(), s.getMedicineName(), next
                        ))
                        .orElse(null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(NextSchedulesResult::nextAt))
                .toList();
    }

}
