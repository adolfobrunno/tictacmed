package com.abba.tictacmed.application.patient.command;

import java.util.UUID;

public record RegisterPatientResult(UUID id, String name, String contact) {
}
