package com.abba.tictacmed.domain.patient.model;

import lombok.Getter;

import java.util.Objects;

/**
 * Patient aggregate root (domain).
 */
@Getter
public final class Patient {

    private final String name;
    private final String contact;

    private Patient(String name, String contact) {
        this.name = validateName(name);
        this.contact = Objects.requireNonNull(contact, "contact is required");
    }

    public static Patient register(String name, String contact) {
        return new Patient(name, contact);
    }

    /**
     * Factory to rehydrate a Patient from persisted state.
     */
    public static Patient fromExisting(String name, String contact) {
        return new Patient(name, contact);
    }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "name is required");
        String trimmed = name.trim();
        if (trimmed.length() < 2) throw new IllegalArgumentException("name too short");
        return trimmed;
    }
}
