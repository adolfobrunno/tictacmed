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
    private final ConfirmationCodeSender confirmationCodeSender;

    @Transactional
    @Override
    public RegisterPatientResult execute(RegisterPatientCommand cmd) {
        Objects.requireNonNull(cmd, "cmd");

        Patient patient;
        if (cmd.selfRegistered()) {
            // Patient initiated their own registration: active immediately
            patient = Patient.selfRegister(cmd.name(), cmd.contact());
        } else {
            // Registration by attendant: pending and requires confirmation
            String code = String.format("%06d", (int) (Math.random() * 1_000_000));
            patient = Patient.registerPending(cmd.name(), cmd.contact(), code);
        }

        patient = patientRepository.save(patient);

        // Send confirmation code only if pending
        if (!patient.isActive() && patient.getConfirmationCode() != null) {
            confirmationCodeSender.sendCode(patient.getContact(), patient.getConfirmationCode());
        }

        return new RegisterPatientResult(patient.getName(), patient.getContact());
    }
}
