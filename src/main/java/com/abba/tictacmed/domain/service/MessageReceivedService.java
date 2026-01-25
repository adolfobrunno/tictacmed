package com.abba.tictacmed.domain.service;

import com.abba.tictacmed.domain.model.MessageReceived;

import java.util.List;

public interface MessageReceivedService {

    void markAsProcessed(String id);

    List<MessageReceived> getPendingMessages();

    void receiveMessage(MessageReceived messageReceived);
}
