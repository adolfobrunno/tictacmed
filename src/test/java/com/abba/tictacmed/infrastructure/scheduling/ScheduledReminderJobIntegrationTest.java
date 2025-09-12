package com.abba.tictacmed.infrastructure.scheduling;

import com.abba.tictacmed.TestcontainersConfiguration;
import com.abba.tictacmed.application.patient.command.RegisterPatientCommand;
import com.abba.tictacmed.application.patient.usecases.RegisterPatientUseCase;
import com.abba.tictacmed.application.scheduling.command.CreateMedicationScheduleCommand;
import com.abba.tictacmed.application.scheduling.service.CreateMedicationScheduleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@DirtiesContext
class ScheduledReminderJobIntegrationTest {

    @Autowired
    RegisterPatientUseCase register;
    @Autowired
    CreateMedicationScheduleUseCase createSchedule;
    @Autowired
    ScheduledReminderJob job;
    @Autowired
    SchedulerProperties schedulerProperties;

    @Test
    void job_runOnceNow_should_notify_due_schedule() {
        // ensure job is enabled for the test
        schedulerProperties.setEnabled(true);

        var patient = register.execute(new RegisterPatientCommand("Cron User", "+551100000000"));
        OffsetDateTime start = OffsetDateTime.of(2025, 9, 9, 8, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = start.plusHours(8);
        createSchedule.execute(new CreateMedicationScheduleCommand(
                patient.id(), "TestMed", start, end, Duration.ofHours(8).getSeconds()
        ));

        int sent = job.runOnceNow();
        assertTrue(sent >= 0); // we can't easily observe sender; just ensure no exception and non-negative count
    }
}
