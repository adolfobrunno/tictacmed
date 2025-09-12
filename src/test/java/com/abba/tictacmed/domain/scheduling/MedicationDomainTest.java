package com.abba.tictacmed.domain.scheduling;

import com.abba.tictacmed.domain.messaging.service.NotificationSender;
import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;
import com.abba.tictacmed.domain.scheduling.service.SchedulingService;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MedicationDomainTest {

    @Test
    void register_patient_create_schedule_and_notify_due() {
        Patient patient = Patient.register("John Doe", "+551199999999");
        OffsetDateTime start = OffsetDateTime.of(2025, 9, 9, 8, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = start.plusDays(1);
        Duration freq = Duration.ofHours(8);

        MedicationSchedule schedule = MedicationSchedule.create(patient, "Amoxicillin", start, end, freq);

        // next due at start
        assertEquals(start, schedule.nextDue(start.minusHours(1)).orElseThrow());

        // create a fake sender to capture notifications
        AtomicReference<OffsetDateTime> notifiedSlot = new AtomicReference<>();
        NotificationSender sender = new NotificationSender() {
            @Override
            public void sendMedicationReminder(UUID scheduleId, Patient p, String med, OffsetDateTime scheduledAt) {
                notifiedSlot.set(scheduledAt);
            }
        };
        SchedulingService service = new SchedulingService(sender);

        OffsetDateTime sentFor = service.notifyNextDue(schedule, start);
        assertEquals(start, sentFor);
        assertEquals(start, notifiedSlot.get());

        // confirm administration then next due shifts by 8h
        schedule.confirmAdministration(start, start.plusMinutes(3));
        assertEquals(start.plusHours(8), schedule.nextDue(start).orElseThrow());
    }

    @Test
    void schedule_alignment_and_bounds() {
        Patient patient = Patient.register("Jane Doe", "jane@example.com");
        OffsetDateTime start = OffsetDateTime.of(2025, 9, 9, 8, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime end = start.plusHours(10);
        Duration freq = Duration.ofHours(2);
        MedicationSchedule schedule = MedicationSchedule.create(patient, "Ibuprofen", start, end, freq);

        // not aligned time should throw
        assertThrows(IllegalArgumentException.class, () -> schedule.confirmAdministration(start.plusHours(1), start.plusHours(1).plusMinutes(5)));
        // out of bounds should throw
        assertThrows(IllegalArgumentException.class, () -> schedule.confirmAdministration(end.plusHours(1), end.plusHours(1)));
    }
}
