package com.abba.tictacmed.application;

import com.abba.tictacmed.TestcontainersConfiguration;
import com.abba.tictacmed.application.patient.command.RegisterPatientCommand;
import com.abba.tictacmed.application.patient.command.RegisterPatientResult;
import com.abba.tictacmed.application.patient.service.RegisterPatientUseCase;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleCommand;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleResult;
import com.abba.tictacmed.application.scheduling.service.CreateMedicationScheduleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@DirtiesContext
class ApplicationUseCasesIntegrationTest {

    @Autowired
    RegisterPatientUseCase registerPatientUseCase;

    @Autowired
    CreateMedicationScheduleUseCase createMedicationScheduleUseCase;

    @Test
    void should_register_patient_and_create_schedule() {
        RegisterPatientResult p = registerPatientUseCase.execute(new RegisterPatientCommand("Alice", "alice@example.com"));
        assertNotNull(p.id());
        ZonedDateTime start = ZonedDateTime.of(2025, 9, 9, 8, 0, 0, 0, ZoneId.of("UTC"));
        ZonedDateTime end = start.plusDays(1);
        CreateMedicationScheduleResult s = createMedicationScheduleUseCase.execute(
                new CreateMedicationScheduleCommand(p.id(), "Paracetamol", start, end, Duration.ofHours(8).getSeconds())
        );
        assertNotNull(s.id());
        assertEquals(p.id(), s.patientId());
        assertEquals("Paracetamol", s.medicineName());
        assertEquals(Duration.ofHours(8), s.frequency());
    }
}
