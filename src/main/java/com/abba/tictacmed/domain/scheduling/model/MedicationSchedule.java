package com.abba.tictacmed.domain.scheduling.model;

import com.abba.tictacmed.domain.patient.model.Patient;
import lombok.Getter;

import java.time.Duration;
import java.time.OffsetDateTime;
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
    private final OffsetDateTime startAt;
    private final OffsetDateTime endAt;
    private final Duration frequency;
    private final boolean recurring;
    private boolean active;

    private final List<AdministrationRecord> administrations = new ArrayList<>();

    public MedicationSchedule(UUID id,
                              Patient patient,
                              String medicineName,
                              OffsetDateTime startAt,
                              OffsetDateTime endAt,
                              Duration frequency,
                              List<AdministrationRecord> administrations,
                              boolean recurring,
                              boolean active) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.patient = Objects.requireNonNull(patient, "patient is required");
        this.medicineName = Objects.requireNonNull(medicineName, "medicineName is required");
        this.startAt = Objects.requireNonNull(startAt, "startAt is required");
        this.endAt = Objects.requireNonNull(endAt, "endAt is required");
        this.frequency = Objects.requireNonNull(frequency, "frequency is required");
        if (!endAt.isAfter(startAt)) throw new IllegalArgumentException("endAt must be after startAt");
        if (frequency.isZero() || frequency.isNegative())
            throw new IllegalArgumentException("frequency must be positive");
        this.administrations.addAll(administrations);
        this.recurring = recurring;
        this.active = active;
    }

    public static MedicationSchedule create(Patient patient,
                                            String medicineName,
                                            OffsetDateTime startAt,
                                            OffsetDateTime endAt,
                                            Duration frequency) {
        List<AdministrationRecord> administrations = new ArrayList<>();
        OffsetDateTime currentDate = startAt;

        var recurring = false;

        if (Objects.isNull(endAt)) {
            recurring = true;
            endAt = startAt.plusMonths(1);
        }

        while (!currentDate.isAfter(endAt)) {
            administrations.add(new AdministrationRecord(medicineName, currentDate, null, AdministrationStatus.SCHEDULED));
            currentDate = currentDate.plus(frequency);
        }

        return new MedicationSchedule(UUID.randomUUID(), patient, medicineName, startAt, endAt, frequency, administrations, recurring, true);
    }

    public Optional<OffsetDateTime> nextDue(OffsetDateTime reference) {
        OffsetDateTime ref = reference == null ? OffsetDateTime.now() : reference;
        if (ref.isAfter(endAt)) return Optional.empty();
        if (ref.isBefore(startAt)) return Optional.of(startAt);
        long elapsed = Duration.between(startAt, ref).getSeconds();
        long step = frequency.getSeconds();
        long steps = (elapsed + step - 1) / step; // ceil
        OffsetDateTime candidate = startAt.plusSeconds(steps * step);
        while (!candidate.isAfter(endAt)) {
            if (!isAdministrationConfirmedAt(candidate)) return Optional.of(candidate);
            candidate = candidate.plus(frequency);
        }
        return Optional.empty();
    }

    public AdministrationRecord confirmAdministration(OffsetDateTime scheduledTime, OffsetDateTime confirmedAt) {
        Objects.requireNonNull(scheduledTime, "scheduledTime is required");
        if (scheduledTime.isBefore(startAt) || scheduledTime.isAfter(endAt)) {
            throw new IllegalArgumentException("scheduledTime out of bounds");
        }
        long fromStart = Duration.between(startAt, scheduledTime).getSeconds();
        long step = frequency.getSeconds();
        if (fromStart % step != 0) throw new IllegalArgumentException("scheduledTime must align with frequency grid");
        return administrations.stream().filter(a -> a.scheduledAt().isEqual(scheduledTime)).findFirst()
                .orElseGet(() -> {
                    AdministrationRecord rec = new AdministrationRecord(this.getMedicineName(), scheduledTime, confirmedAt == null ? OffsetDateTime.now() : confirmedAt, AdministrationStatus.CONFIRMED);
                    administrations.add(rec);
                    return rec;
                });
    }

    public AdministrationRecord skipAdministration(OffsetDateTime scheduledTime) {
        Objects.requireNonNull(scheduledTime, "scheduledTime is required");
        if (scheduledTime.isBefore(startAt) || scheduledTime.isAfter(endAt)) {
            throw new IllegalArgumentException("scheduledTime out of bounds");
        }
        long fromStart = Duration.between(startAt, scheduledTime).getSeconds();
        long step = frequency.getSeconds();
        if (fromStart % step != 0) throw new IllegalArgumentException("scheduledTime must align with frequency grid");
        // If already has a record (any status), just return it
        return administrations.stream().filter(a -> a.scheduledAt().isEqual(scheduledTime)).findFirst()
                .orElseGet(() -> {
                    AdministrationRecord rec = new AdministrationRecord(this.getMedicineName(), scheduledTime, null, AdministrationStatus.SKIPPED);
                    administrations.add(rec);
                    return rec;
                });
    }

    public boolean isAdministrationConfirmedAt(OffsetDateTime scheduledTime) {
        return administrations.stream().anyMatch(a -> a.scheduledAt().isEqual(scheduledTime));
    }

    public void deactivate() {
        this.active = false;
    }

    public void cancelPendingAdministrations() {
        for (int i = 0; i < administrations.size(); i++) {
            var ar = administrations.get(i);
            if (ar.status() == AdministrationStatus.SCHEDULED) {
                administrations.set(i, new AdministrationRecord(ar.medicineName(), ar.scheduledAt(), ar.confirmedAt(), AdministrationStatus.CANCELED));
            }
        }
    }

    public record AdministrationRecord(String medicineName, OffsetDateTime scheduledAt, OffsetDateTime confirmedAt,
                                       AdministrationStatus status) {
    }
}
