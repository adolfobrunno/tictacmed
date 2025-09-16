package com.abba.tictacmed.domain.patient.model;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

/**
 * Patient aggregate root (domain).
 */
@Getter
public final class Patient {

    private final UUID id;
    private final String name;
    private final String contact;

    private Patient(UUID id, String name, String contact) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = validateName(name);
        this.contact = Objects.requireNonNull(contact, "contact is required");
    }

    public static Patient register(String name, String contact) {
        return new Patient(UUID.randomUUID(), name, contact);
    }

    /**
     * Factory to rehydrate a Patient from persisted state.
     */
    public static Patient fromExisting(UUID id, String name, String contact) {
        return new Patient(id, name, contact);
    }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "name is required");
        String trimmed = name.trim();
        if (trimmed.length() < 2) throw new IllegalArgumentException("name too short");
        return trimmed;
    }
}
