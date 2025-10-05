package com.abba.tictacmed.application.patient.service;

public interface ConfirmationCodeSender {
    void sendCode(String contact, String code);
}
