package com.abba.tictacmed.domain.scheduling.model;

import com.abba.tictacmed.domain.patient.model.Patient;
import lombok.Getter;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Represents a medication schedule for a patient between a start and end time using a fixed frequency.
 * It tracks confirmations and can compute next due time.
 */
@Getter
public final class MedicationSchedule {
    private final UUID id;
    private final Patient patient;
    private final String medicineName;
    private final ZonedDateTime startAt;
    private final ZonedDateTime endAt;
    private final Duration frequency;

    private final List<AdministrationRecord> administrations = new ArrayList<>();

    public MedicationSchedule(UUID id, Patient patient, String medicineName, ZonedDateTime startAt, ZonedDateTime endAt, Duration frequency) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.patient = Objects.requireNonNull(patient, "patient is required");
        this.medicineName = Objects.requireNonNull(medicineName, "medicineName is required");
        this.startAt = Objects.requireNonNull(startAt, "startAt is required");
        this.endAt = Objects.requireNonNull(endAt, "endAt is required");
        this.frequency = Objects.requireNonNull(frequency, "frequency is required");
        if (!endAt.isAfter(startAt)) throw new IllegalArgumentException("endAt must be after startAt");
        if (frequency.isZero() || frequency.isNegative())
            throw new IllegalArgumentException("frequency must be positive");
    }

    public static MedicationSchedule create(Patient patient, String medicineName, ZonedDateTime startAt, ZonedDateTime endAt, Duration frequency) {
        return new MedicationSchedule(UUID.randomUUID(), patient, medicineName, startAt, endAt, frequency);
    }


    public List<AdministrationRecord> getAdministrations() {
        return Collections.unmodifiableList(administrations);
    }

    public Optional<ZonedDateTime> nextDue(ZonedDateTime reference) {
        ZonedDateTime ref = reference == null ? ZonedDateTime.now() : reference;
        if (ref.isAfter(endAt)) return Optional.empty();
        if (ref.isBefore(startAt)) return Optional.of(startAt);
        long elapsed = java.time.Duration.between(startAt, ref).getSeconds();
        long step = frequency.getSeconds();
        long steps = (elapsed + step - 1) / step; // ceil
        ZonedDateTime candidate = startAt.plusSeconds(steps * step);
        while (!candidate.isAfter(endAt)) {
            if (!isAdministrationConfirmedAt(candidate)) return Optional.of(candidate);
            candidate = candidate.plus(frequency);
        }
        return Optional.empty();
    }

    public AdministrationRecord confirmAdministration(ZonedDateTime scheduledTime, ZonedDateTime confirmedAt) {
        Objects.requireNonNull(scheduledTime, "scheduledTime is required");
        if (scheduledTime.isBefore(startAt) || scheduledTime.isAfter(endAt)) {
            throw new IllegalArgumentException("scheduledTime out of bounds");
        }
        long fromStart = java.time.Duration.between(startAt, scheduledTime).getSeconds();
        long step = frequency.getSeconds();
        if (fromStart % step != 0) throw new IllegalArgumentException("scheduledTime must align with frequency grid");
        return administrations.stream().filter(a -> a.scheduledAt().isEqual(scheduledTime)).findFirst()
                .orElseGet(() -> {
                    AdministrationRecord rec = new AdministrationRecord(scheduledTime, confirmedAt == null ? ZonedDateTime.now() : confirmedAt);
                    administrations.add(rec);
                    return rec;
                });
    }

    public boolean isAdministrationConfirmedAt(ZonedDateTime scheduledTime) {
        return administrations.stream().anyMatch(a -> a.scheduledAt().isEqual(scheduledTime));
    }

    public record AdministrationRecord(ZonedDateTime scheduledAt, ZonedDateTime confirmedAt) {
        public AdministrationRecord {
            Objects.requireNonNull(scheduledAt, "scheduledAt is required");
            Objects.requireNonNull(confirmedAt, "confirmedAt is required");
        }
    }
}
