package com.abba.tictacmed.domain.messaging.model;

import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class WhatsAppMessage {

    private final String id;
    private final String from;
    private final String body;
    private final ZonedDateTime receivedAt;
    private ZonedDateTime processedAt;
    private WhatsAppMessageStatus status;

    private WhatsAppMessage(String id, String from, String body, ZonedDateTime receivedAt, ZonedDateTime processedAt, WhatsAppMessageStatus status) {
        this.id = id;
        this.from = from;
        this.body = body;
        this.receivedAt = receivedAt;
        this.processedAt = processedAt;
        this.status = status;
    }

    public static WhatsAppMessage register(String id, String from, String body) {
        return new WhatsAppMessage(id, from, body, ZonedDateTime.now(), null, WhatsAppMessageStatus.UNPROCESSED);
    }

    /**
     * Rehydrate from persistence layer
     */
    public static WhatsAppMessage fromExisting(String id, String from, String body, ZonedDateTime receivedAt, ZonedDateTime processedAt, WhatsAppMessageStatus status) {
        return new WhatsAppMessage(id, from, body, receivedAt, processedAt, status);
    }

    public void markProcessed() {
        this.status = WhatsAppMessageStatus.PROCESSED;
        this.processedAt = ZonedDateTime.now();
    }

}
