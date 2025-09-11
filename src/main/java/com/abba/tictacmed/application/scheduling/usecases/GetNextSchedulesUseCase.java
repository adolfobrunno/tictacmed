package com.abba.tictacmed.application.scheduling.usecases;

import com.abba.tictacmed.application.scheduling.command.NextSchedulesResult;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface GetNextSchedulesUseCase {
    List<NextSchedulesResult> execute(UUID patientId, ZonedDateTime from, ZonedDateTime to);
}
