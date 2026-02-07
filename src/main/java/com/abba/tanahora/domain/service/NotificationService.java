package com.abba.tanahora.domain.service;

import com.abba.tanahora.application.notification.WhatsAppMessage;
import com.abba.tanahora.domain.model.User;

public interface NotificationService {

    String sendNotification(User user, WhatsAppMessage message);

}
