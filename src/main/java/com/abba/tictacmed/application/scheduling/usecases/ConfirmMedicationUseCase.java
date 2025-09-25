package com.abba.tictacmed.application.scheduling.usecases;

import com.abba.tictacmed.application.scheduling.command.ConfirmMedicationCommand;

public interface ConfirmMedicationUseCase {

    void execute(ConfirmMedicationCommand cmd);

}
