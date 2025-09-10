package com.abba.tictacmed.application.scheduling.command;

import java.time.ZonedDateTime;
import java.util.UUID;

public record CreateMedicationScheduleCommand(
        UUID patientId,
        String medicineName,
        ZonedDateTime startAt,
        ZonedDateTime endAt,
        long frequencySeconds
) {
}
