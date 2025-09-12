package com.abba.tictacmed.infrastructure.messaging;

import com.abba.tictacmed.TestcontainersConfiguration;
import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;
import com.abba.tictacmed.domain.scheduling.service.SchedulingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@DirtiesContext
class MessagingWiringIntegrationTest {

    @Autowired
    SchedulingService schedulingService;

    @Test
    void scheduling_service_and_whatsapp_sender_are_wired_and_invocable() {
        assertNotNull(schedulingService);
        Patient p = Patient.register("Bob", "+55000000000");
        OffsetDateTime start = OffsetDateTime.of(2025, 9, 9, 8, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = start.plusDays(1);
        MedicationSchedule schedule = MedicationSchedule.create(p, "TestMed", start, end, Duration.ofHours(8));
        // Should not throw even with WhatsApp disabled by default
        assertDoesNotThrow(() -> schedulingService.notifyNextDue(schedule, start.minusMinutes(1)));
    }
}
