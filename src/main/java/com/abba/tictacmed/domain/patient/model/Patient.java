package com.abba.tictacmed.domain.patient.model;

import lombok.Getter;

import java.util.Objects;

/**
 * Patient aggregate root (domain).
 */
@Getter
public final class Patient {

    public enum Status {PENDING_CONFIRMATION, ACTIVE}

    private final String name;
    private final String contact;
    private final Status status;
    private final String confirmationCode; // nullable when ACTIVE or not required

    private Patient(String name, String contact, Status status, String confirmationCode) {
        this.name = validateName(name);
        this.contact = Objects.requireNonNull(contact, "contact is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.confirmationCode = confirmationCode; // can be null
    }

    // Registration initiated by pharmacy attendant: create pending with code
    public static Patient registerPending(String name, String contact, String code) {
        if (code == null || code.isBlank())
            throw new IllegalArgumentException("confirmation code is required for pending registration");
        return new Patient(name, contact, Status.PENDING_CONFIRMATION, code);
    }

    // Self-registration (e.g., via WhatsApp or patient portal): already active
    public static Patient selfRegister(String name, String contact) {
        return new Patient(name, contact, Status.ACTIVE, null);
    }

    /**
     * Factory to rehydrate a Patient from persisted state.
     */
    public static Patient fromExisting(String name, String contact, Status status, String confirmationCode) {
        return new Patient(name, contact, status, confirmationCode);
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    public Patient confirm(String code) {
        if (!Objects.equals(this.confirmationCode, code)) {
            throw new IllegalArgumentException("Invalid confirmation code");
        }
        return new Patient(this.name, this.contact, Status.ACTIVE, null);
    }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "name is required");
        String trimmed = name.trim();
        if (trimmed.length() < 2) throw new IllegalArgumentException("name too short");
        return trimmed;
    }
}
