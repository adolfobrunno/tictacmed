package com.abba.tictacmed.application.patient.usecases;

import com.abba.tictacmed.application.patient.command.ConfirmPatientCommand;

public interface ConfirmPatientUseCase {
    void execute(ConfirmPatientCommand cmd);
}
