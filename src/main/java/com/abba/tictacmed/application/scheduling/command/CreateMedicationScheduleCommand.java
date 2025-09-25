package com.abba.tictacmed.application.scheduling.command;

import java.time.Duration;
import java.time.OffsetDateTime;

public record CreateMedicationScheduleCommand(
        String patientContact,
        String medicineName,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        Duration frequency,
        boolean recurring) {
}
