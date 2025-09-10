package com.abba.tictacmed.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "administration_record")
public class AdministrationRecordEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.BINARY)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false, columnDefinition = "BINARY(16)")
    private MedicationScheduleEntity schedule;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(name = "confirmed_at", nullable = false)
    private OffsetDateTime confirmedAt;

    protected AdministrationRecordEntity() {
    }

    public AdministrationRecordEntity(UUID id, MedicationScheduleEntity schedule, OffsetDateTime scheduledAt, OffsetDateTime confirmedAt) {
        this.id = id;
        this.schedule = schedule;
        this.scheduledAt = scheduledAt;
        this.confirmedAt = confirmedAt;
    }

    public UUID getId() {
        return id;
    }

    public MedicationScheduleEntity getSchedule() {
        return schedule;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public OffsetDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setSchedule(MedicationScheduleEntity schedule) {
        this.schedule = schedule;
    }

    public void setScheduledAt(OffsetDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public void setConfirmedAt(OffsetDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }
}
