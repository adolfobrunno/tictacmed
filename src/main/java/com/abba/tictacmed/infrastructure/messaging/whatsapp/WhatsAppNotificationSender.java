package com.abba.tictacmed.infrastructure.messaging.whatsapp;

import com.abba.tictacmed.domain.messaging.NotificationSender;
import com.abba.tictacmed.domain.patient.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * Simple WhatsApp notification sender adapter. For now, it logs the message that would be sent.
 * It is controlled by properties (enabled flag) to avoid external calls in tests and local runs.
 */
public class WhatsAppNotificationSender implements NotificationSender {
    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationSender.class);
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private final WhatsAppProperties properties;

    public WhatsAppNotificationSender(WhatsAppProperties properties) {
        this.properties = Objects.requireNonNull(properties);
    }

    @Override
    public void sendMedicationReminder(UUID scheduleId, Patient patient, String medicineName, ZonedDateTime scheduledAt) {
        if (!properties.isEnabled()) {
            log.info("[WhatsApp disabled] Would send reminder: scheduleId={}, to={}, med={}, at={}",
                    scheduleId, safe(patient.getContact()), medicineName, ISO_FMT.format(scheduledAt));
            return;
        }
        String body = properties.getTemplate()
                .replace("{patientName}", safe(patient.getName()))
                .replace("{medicineName}", String.valueOf(medicineName))
                .replace("{scheduledAt}", ISO_FMT.format(scheduledAt));
        log.info("Sending WhatsApp message from={} to={} body={}", safe(properties.getFromNumber()), safe(patient.getContact()), body);
        // Here you would integrate with a provider SDK/API (e.g., Twilio/Meta), handling errors and retries.
        // TODO Implement this.
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
