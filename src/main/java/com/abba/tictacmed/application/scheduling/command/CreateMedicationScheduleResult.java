package com.abba.tictacmed.application.scheduling.command;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateMedicationScheduleResult(
        UUID id,
        UUID patientId,
        String medicineName,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        Duration frequency
) {
}
