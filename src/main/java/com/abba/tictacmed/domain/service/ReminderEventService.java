package com.abba.tictacmed.domain.service;

import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.ReminderEvent;

import java.util.Optional;

public interface ReminderEventService {

    ReminderEvent registerDispatch(Reminder reminder, String whatsappMessageId);

    Optional<ReminderEvent> findPendingByReminder(Reminder reminder);

    ReminderEvent updateDispatch(ReminderEvent event, String whatsappMessageId);

    Optional<ReminderEvent> updateStatusFromResponse(String replyToMessageId, String responseText);
}
