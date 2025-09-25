package com.abba.tictacmed.application.scheduling.command;

public record ConfirmMedicationCommand(String patientContact, String medicineName, boolean confirmed) {
}
