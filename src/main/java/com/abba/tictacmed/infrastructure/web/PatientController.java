package com.abba.tictacmed.infrastructure.web;

import com.abba.tictacmed.application.patient.command.RegisterPatientCommand;
import com.abba.tictacmed.application.patient.command.RegisterPatientResult;
import com.abba.tictacmed.application.patient.service.RegisterPatientUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final RegisterPatientUseCase registerPatientUseCase;

    public PatientController(RegisterPatientUseCase registerPatientUseCase) {
        this.registerPatientUseCase = registerPatientUseCase;
    }

    public record RegisterPatientRequest(String name, String contact) {
    }

    @PostMapping
    public ResponseEntity<RegisterPatientResult> register(@RequestBody RegisterPatientRequest request) {
        RegisterPatientResult result = registerPatientUseCase.execute(new RegisterPatientCommand(request.name(), request.contact()));
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
