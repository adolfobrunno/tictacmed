package com.abba.tictacmed.application.patient.service;

import com.abba.tictacmed.application.patient.command.ConfirmPatientCommand;
import com.abba.tictacmed.application.patient.usecases.ConfirmPatientUseCase;
import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ConfirmPatientUseCaseImpl implements ConfirmPatientUseCase {

    private final PatientRepository patientRepository;

    @Override
    @Transactional
    public void execute(ConfirmPatientCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");
        var patient = patientRepository.findByContact(cmd.contact())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + cmd.contact()));
        if (patient.isActive()) return; // already active
        Patient confirmed = patient.confirm(cmd.code());
        patientRepository.save(confirmed);
    }
}
