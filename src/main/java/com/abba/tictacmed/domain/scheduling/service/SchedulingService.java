package com.abba.tictacmed.domain.scheduling.service;

import com.abba.tictacmed.domain.messaging.service.NotificationSender;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Domain service to evaluate schedules and trigger reminder notifications for due doses.
 */
public final class SchedulingService {

    private final NotificationSender notificationSender;

    public SchedulingService(NotificationSender notificationSender) {
        this.notificationSender = Objects.requireNonNull(notificationSender, "notificationSender");
    }

    /**
     * If a schedule has a dose due at or after the reference time, send a reminder for the earliest due slot.
     * Returns the scheduledAt that was notified, or null if none due.
     */
    public OffsetDateTime notifyNextDue(MedicationSchedule schedule, OffsetDateTime reference) {
        return schedule.nextDue(reference)
                .map(scheduledAt -> {
                    notificationSender.sendMedicationReminder(schedule.getId(), schedule.getPatient(), schedule.getMedicineName(), scheduledAt);
                    return scheduledAt;
                })
                .orElse(null);
    }
}
