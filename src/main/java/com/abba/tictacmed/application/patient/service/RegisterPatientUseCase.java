package com.abba.tictacmed.application.patient.service;

import com.abba.tictacmed.application.patient.command.RegisterPatientCommand;
import com.abba.tictacmed.application.patient.command.RegisterPatientResult;
import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RegisterPatientUseCase {

    private final PatientRepository patientRepository;

    public RegisterPatientUseCase(PatientRepository patientRepository) {
        this.patientRepository = Objects.requireNonNull(patientRepository);
    }

    @Transactional
    public RegisterPatientResult execute(RegisterPatientCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        Patient patient = Patient.register(cmd.name(), cmd.contact());
        patient = patientRepository.save(patient);
        return new RegisterPatientResult(patient.getId(), patient.getName(), patient.getContact());
    }
}
