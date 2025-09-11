package com.abba.tictacmed.application.patient.usecases;

import com.abba.tictacmed.application.patient.command.RegisterPatientCommand;
import com.abba.tictacmed.application.patient.command.RegisterPatientResult;

public interface RegisterPatientUseCase {
    RegisterPatientResult execute(RegisterPatientCommand cmd);
}
