package com.abba.tanahora.application.notification;

public interface WhatsAppMessage {

    String buildPayload();
    WhatsAppMessageType getType();

}
