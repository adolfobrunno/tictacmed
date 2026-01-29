package com.abba.tanahora.application.exceptions;

public class ReminderLimitException extends RuntimeException {
    public ReminderLimitException(String message) {
        super(message);
    }
}
