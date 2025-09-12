package com.abba.tictacmed.application.scheduling.usecases;

import com.abba.tictacmed.application.scheduling.command.NextSchedulesResult;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface GetNextSchedulesUseCase {
    NextSchedulesResult execute(UUID patientId, OffsetDateTime from, OffsetDateTime to);
}
