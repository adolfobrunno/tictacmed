package com.abba.tanahora.domain.service;

import com.abba.tanahora.domain.model.Reminder;
import com.abba.tanahora.domain.model.ReminderEvent;

import java.util.Optional;

public interface ReminderEventService {

    ReminderEvent registerDispatch(Reminder reminder, String whatsappMessageId);

    Optional<ReminderEvent> findPendingByReminder(Reminder reminder);

    ReminderEvent updateDispatch(ReminderEvent event, String whatsappMessageId);

    Optional<ReminderEvent> updateStatusFromResponse(String replyToMessageId, String responseText);

    Optional<ReminderEvent> updateLastPending(String whatsappId, String responseText);
}
