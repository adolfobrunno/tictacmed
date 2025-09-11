package com.abba.tictacmed.domain.messaging.repository;

import com.abba.tictacmed.domain.messaging.model.WhatsAppMessage;

import java.util.Set;

public interface WhatsAppMessageRepository {

    void save(WhatsAppMessage message);

    Set<WhatsAppMessage> findAllUnprocessed();
}
