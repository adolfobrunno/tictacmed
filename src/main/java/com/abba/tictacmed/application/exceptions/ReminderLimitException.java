package com.abba.tictacmed.application.exceptions;

public class ReminderLimitException extends RuntimeException {
    public ReminderLimitException(String message) {
        super(message);
    }
}
