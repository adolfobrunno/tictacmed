package com.abba.tictacmed.application.service;

import com.abba.tictacmed.application.notification.WhatsAppGateway;
import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.User;
import com.abba.tictacmed.domain.service.NotificationService;
import com.abba.tictacmed.domain.service.ReminderEventService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final WhatsAppGateway whatsAppGateway;
    private final ReminderEventService reminderEventService;

    public NotificationServiceImpl(WhatsAppGateway whatsAppGateway, ReminderEventService reminderEventService) {
        this.whatsAppGateway = whatsAppGateway;
        this.reminderEventService = reminderEventService;
    }

    @Override
    public void sendNotification(User user, Reminder reminder) {
        String messageId = whatsAppGateway.sendReminder(user, reminder);
        reminderEventService.registerDispatch(reminder, messageId);
    }
}
