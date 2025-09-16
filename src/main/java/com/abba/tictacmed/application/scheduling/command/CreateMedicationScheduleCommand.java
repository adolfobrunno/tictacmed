package com.abba.tictacmed.application.scheduling.command;

import java.time.OffsetDateTime;

public record CreateMedicationScheduleCommand(
        String patientContact,
        String medicineName,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        long frequencySeconds
) {
}
