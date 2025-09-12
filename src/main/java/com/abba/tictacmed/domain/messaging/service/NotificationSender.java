package com.abba.tictacmed.domain.messaging.service;

import com.abba.tictacmed.domain.patient.model.Patient;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Port to send notification messages to patients.
 */
public interface NotificationSender {
    void sendMedicationReminder(UUID scheduleId, Patient patient, String medicineName, OffsetDateTime scheduledAt);
}
