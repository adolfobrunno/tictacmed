package com.abba.tictacmed.domain.service;

import com.abba.tictacmed.domain.model.MessageReceived;

public interface MessageReceivedHandler {

    void handle(MessageReceived messageReceived);
}
