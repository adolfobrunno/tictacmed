package com.abba.tictacmed.application.patient.command;

public record RegisterPatientCommand(
        String name,
        String contact,
        boolean selfRegistered
) {
}
