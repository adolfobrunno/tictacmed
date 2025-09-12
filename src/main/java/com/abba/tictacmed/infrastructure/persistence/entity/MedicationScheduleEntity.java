package com.abba.tictacmed.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "medication_schedule")
public class MedicationScheduleEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
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

}
