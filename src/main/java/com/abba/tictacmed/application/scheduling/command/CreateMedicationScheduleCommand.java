package com.abba.tictacmed.application.scheduling.command;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateMedicationScheduleCommand(
        UUID patientId,
        String medicineName,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        long frequencySeconds
) {
}
