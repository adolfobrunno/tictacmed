package com.abba.tictacmed.infrastructure.persistence.entity;

import com.abba.tictacmed.domain.scheduling.model.AdministrationStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "administration_record")
public class AdministrationRecordEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false, columnDefinition = "BINARY(16)")
    private MedicationScheduleEntity schedule;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(name = "confirmed_at", nullable = false)
    private OffsetDateTime confirmedAt;

    @Enumerated(EnumType.STRING)
    private AdministrationStatus status;

    protected AdministrationRecordEntity() {
    }

    public AdministrationRecordEntity(UUID id, MedicationScheduleEntity schedule, OffsetDateTime scheduledAt, OffsetDateTime confirmedAt, AdministrationStatus status) {
        this.id = id;
        this.schedule = schedule;
        this.scheduledAt = scheduledAt;
        this.confirmedAt = confirmedAt;
        this.status = status;
    }

}
