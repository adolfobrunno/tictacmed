package com.abba.tictacmed.application.notification;

import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.User;

public interface WhatsAppGateway {

    void sendReminder(User user, Reminder reminder);

}
