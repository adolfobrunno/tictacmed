package com.abba.tictacmed.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "medication_schedule")
public class MedicationScheduleEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.BINARY)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, columnDefinition = "BINARY(16)")
    private PatientEntity patient;

    @Column(name = "medicine_name", nullable = false, length = 200)
    private String medicineName;

    @Column(name = "start_at", nullable = false)
    private OffsetDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private OffsetDateTime endAt;

    @Column(name = "frequency_seconds", nullable = false)
    private long frequencySeconds;

    protected MedicationScheduleEntity() {
    }

    public MedicationScheduleEntity(UUID id, PatientEntity patient, String medicineName, OffsetDateTime startAt, OffsetDateTime endAt, long frequencySeconds) {
        this.id = id;
        this.patient = patient;
        this.medicineName = medicineName;
        this.startAt = startAt;
        this.endAt = endAt;
        this.frequencySeconds = frequencySeconds;
    }

    public UUID getId() {
        return id;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public OffsetDateTime getStartAt() {
        return startAt;
    }

    public OffsetDateTime getEndAt() {
        return endAt;
    }

    public long getFrequencySeconds() {
        return frequencySeconds;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public void setStartAt(OffsetDateTime startAt) {
        this.startAt = startAt;
    }

    public void setEndAt(OffsetDateTime endAt) {
        this.endAt = endAt;
    }

    public void setFrequencySeconds(long frequencySeconds) {
        this.frequencySeconds = frequencySeconds;
    }
}
