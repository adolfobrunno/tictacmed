package com.abba.tictacmed.application.scheduling.command;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

public record CreateMedicationScheduleResult(
        UUID id,
        UUID patientId,
        String medicineName,
        ZonedDateTime startAt,
        ZonedDateTime endAt,
        Duration frequency
) {
}
