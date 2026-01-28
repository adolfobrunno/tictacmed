package com.abba.tictacmed.domain.exceptions;

public class InvalidRruleException extends RuntimeException {
    public InvalidRruleException(String message) {
        super(message);
    }

    public InvalidRruleException(String message, Throwable cause) {
        super(message, cause);
    }
}
