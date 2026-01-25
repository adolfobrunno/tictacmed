package com.abba.tictacmed.domain.service;

import com.abba.tictacmed.domain.model.MessageReceived;

import java.util.List;

public interface MessageReceivedService {

    void receiveMessage(String message, String whatsappId);

    void markAsProcessed(String id);

    List<MessageReceived> getPendingMessages();

}
