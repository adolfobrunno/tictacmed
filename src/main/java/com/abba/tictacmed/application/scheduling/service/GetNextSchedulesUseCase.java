package com.abba.tictacmed.application.scheduling.service;

import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class GetNextSchedulesUseCase {

    private final MedicationScheduleRepository scheduleRepository;

    public GetNextSchedulesUseCase(MedicationScheduleRepository scheduleRepository) {
        this.scheduleRepository = Objects.requireNonNull(scheduleRepository);
    }

    public List<NextScheduleDto> execute(UUID patientId, ZonedDateTime from, ZonedDateTime to) {
        Objects.requireNonNull(patientId, "patientId");
        ZonedDateTime start = from == null ? ZonedDateTime.now() : from;
        ZonedDateTime end = to == null ? start.plusDays(1) : to;
        if (!end.isAfter(start)) throw new IllegalArgumentException("to must be after from");

        return scheduleRepository.findByPatientId(patientId).stream()
                .map(s -> s.nextDue(start)
                        .filter(next -> !next.isAfter(end))
                        .map(next -> new NextScheduleDto(
                                s.getId(), s.getPatient().getId(), s.getMedicineName(), next
                        ))
                        .orElse(null))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(NextScheduleDto::nextAt))
                .toList();
    }

    public record NextScheduleDto(UUID scheduleId, UUID patientId, String medicineName, ZonedDateTime nextAt) {
    }
}
