package com.abba.tictacmed.application.scheduling.command;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NextSchedulesResult(UUID scheduleId, UUID patientId, String medicineName, OffsetDateTime nextAt) {
}
