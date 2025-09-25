package com.abba.tictacmed.application.messaging.command;

public record RegisterMessageReceivedCommand(String messageId, String from, String body, String contact) {
}
