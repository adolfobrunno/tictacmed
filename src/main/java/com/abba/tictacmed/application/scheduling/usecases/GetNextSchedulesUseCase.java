package com.abba.tictacmed.application.scheduling.usecases;

import com.abba.tictacmed.application.scheduling.command.NextSchedulesResult;

import java.time.OffsetDateTime;

public interface GetNextSchedulesUseCase {
    NextSchedulesResult execute(String patientId, OffsetDateTime from, OffsetDateTime to);
}
