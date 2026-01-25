package com.abba.tictacmed.domain.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.abba.tictacmed.domain.model.Medication;
import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.User;

public interface ReminderService {

    Reminder scheduleMedication(User usuario, Medication med, String rrule);
    Optional<OffsetDateTime> getNextDispatch(Reminder reminder);
    List<Reminder> getTodayPendingReminders();

}
