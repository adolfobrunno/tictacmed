package com.abba.tictacmed.application.scheduling.command;

import java.time.OffsetDateTime;

public record NextSchedulesResult(String medicineName, OffsetDateTime nextAt) {
}
