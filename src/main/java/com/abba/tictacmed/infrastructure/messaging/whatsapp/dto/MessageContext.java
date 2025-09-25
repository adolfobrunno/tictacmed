package com.abba.tictacmed.infrastructure.messaging.whatsapp.dto;

public record MessageContext(String contactName, String contactNumber, MessageReceived messageReceived) {
}
