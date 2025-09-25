package com.abba.tictacmed.application.scheduling.usecases;

import com.abba.tictacmed.application.scheduling.command.RemoveMedicationScheduleCommand;

public interface RemoveMedicationScheduleUseCase {

    void execute(RemoveMedicationScheduleCommand cmd);

}
