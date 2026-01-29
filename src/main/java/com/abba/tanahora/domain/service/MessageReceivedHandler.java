package com.abba.tanahora.domain.service;

import com.abba.tanahora.domain.model.MessageReceived;

public interface MessageReceivedHandler {

    void handle(MessageReceived messageReceived);
}
