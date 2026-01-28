package com.abba.tictacmed.domain.service;

import com.abba.tictacmed.domain.model.User;

public interface NotificationService {

    String sendNotification(User user, String message);

}
