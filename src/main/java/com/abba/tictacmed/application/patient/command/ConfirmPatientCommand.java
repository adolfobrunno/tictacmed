package com.abba.tictacmed.application.patient.command;

public record ConfirmPatientCommand(
        String contact,
        String code
) {
}
