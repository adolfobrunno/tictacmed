package com.abba.tictacmed.infrastructure.messaging;

import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.WhatsAppNotificationSender;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.WhatsAppProperties;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class WhatsAppNotificationSenderTest {

    @Test
    void should_not_throw_when_disabled_and_log_message() {
        WhatsAppProperties props = new WhatsAppProperties();
        props.setEnabled(false);
        props.setFromNumber("+1111111111");
        props.setTemplate("Hi {patientName}, take {medicineName} at {scheduledAt}");
        WhatsAppNotificationSender sender = new WhatsAppNotificationSender(props);

        Patient p = Patient.fromExisting(UUID.randomUUID(), "Test User", "+551199999999");
        OffsetDateTime slot = OffsetDateTime.of(2025, 9, 9, 8, 0, 0, 0, ZoneOffset.UTC);
        assertDoesNotThrow(() -> sender.sendMedicationReminder(UUID.randomUUID(), p, "Ibuprofen", slot));
    }
}
