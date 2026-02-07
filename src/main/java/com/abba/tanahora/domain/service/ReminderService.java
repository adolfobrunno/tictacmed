package com.abba.tanahora.domain.service;

import com.abba.tanahora.domain.model.Medication;
import com.abba.tanahora.domain.model.PatientRef;
import com.abba.tanahora.domain.model.Reminder;
import com.abba.tanahora.domain.model.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface ReminderService {

    Reminder scheduleMedication(User user, PatientRef patient, Medication med, String rrule);
    Optional<OffsetDateTime> getNextDispatch(Reminder reminder);

    List<Reminder> getNextRemindersToNotify();

    List<Reminder> getByUser(User user);
    void cancelReminder(Reminder reminder);

    void updateReminderNextDispatch(Reminder reminder);

}
