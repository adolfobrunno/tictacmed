package com.abba.tictacmed.application.scheduling.usecases;

import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleCommand;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleResult;

public interface CreateMedicationScheduleUseCase {
    CreateMedicationScheduleResult execute(CreateMedicationScheduleCommand cmd);
}
