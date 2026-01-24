package com.abba.tictacmed.domain.service;

import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.User;

public interface NotificationService {

    void sendNotification(User user, Reminder reminder);

}
