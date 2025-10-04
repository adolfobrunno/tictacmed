package com.abba.tictacmed.infrastructure.messaging.whatsapp;

import com.abba.tictacmed.domain.messaging.service.NotificationSender;
import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.infrastructure.utils.Buttons;
import com.whatsapp.api.domain.messages.Button;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Simple WhatsApp notification sender adapter. For now, it logs the message that would be sent.
 * It is controlled by properties (enabled flag) to avoid external calls in tests and local runs.
 */
@RequiredArgsConstructor
public class WhatsAppNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppNotificationSender.class);
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final WhatsAppProperties properties;
    private final WhatsAppClient whatsAppClient;


    @Override
    public void sendMedicationReminder(UUID scheduleId, Patient patient, String medicineName, OffsetDateTime scheduledAt) {
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

        Button buttonConfirm = Buttons.createConfirmButton(patient, medicineName);
        Button buttonSkip = Buttons.createSkipButton(patient, medicineName);

        whatsAppClient.sendInteractive(patient.getContact(), body, List.of(buttonConfirm, buttonSkip));
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
