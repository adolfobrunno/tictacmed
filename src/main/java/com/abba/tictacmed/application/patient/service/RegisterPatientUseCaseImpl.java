package com.abba.tictacmed.application.patient.service;

import com.abba.tictacmed.application.patient.command.RegisterPatientCommand;
import com.abba.tictacmed.application.patient.command.RegisterPatientResult;
import com.abba.tictacmed.application.patient.usecases.RegisterPatientUseCase;
import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegisterPatientUseCaseImpl implements RegisterPatientUseCase {

    private final PatientRepository patientRepository;

    @Transactional
    @Override
    public RegisterPatientResult execute(RegisterPatientCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        Patient patient = Patient.register(cmd.name(), cmd.contact());
        patient = patientRepository.save(patient);
        return new RegisterPatientResult(patient.getName(), patient.getContact());
    }
}
