package com.abba.tictacmed.application.service;

import com.abba.tictacmed.application.notification.WhatsAppGateway;
import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.User;
import com.abba.tictacmed.domain.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final WhatsAppGateway whatsAppGateway;

    public NotificationServiceImpl(WhatsAppGateway whatsAppGateway) {
        this.whatsAppGateway = whatsAppGateway;
    }

    @Override
    public void sendNotification(User user, Reminder reminder) {
        whatsAppGateway.sendReminder(user, reminder);
    }
}
