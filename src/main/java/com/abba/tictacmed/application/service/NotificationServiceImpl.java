package com.abba.tictacmed.application.service;

import com.abba.tictacmed.application.notification.WhatsAppGateway;
import com.abba.tictacmed.domain.model.User;
import com.abba.tictacmed.domain.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final WhatsAppGateway whatsAppGateway;

    public NotificationServiceImpl(WhatsAppGateway whatsAppGateway) {
        this.whatsAppGateway = whatsAppGateway;
    }


    @Override
    public String sendNotification(User user, String message) {
        return whatsAppGateway.sendMessage(user, message);
    }

}
