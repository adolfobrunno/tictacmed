package com.abba.tictacmed.infrastructure.persistence.entity;

import com.abba.tictacmed.domain.scheduling.model.AdministrationStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Document(collection = "administration_record")
public class AdministrationRecordEntity {

    @Id
    private UUID id;

    @DBRef
    private MedicationScheduleEntity schedule;

    private OffsetDateTime scheduledAt;

    private OffsetDateTime confirmedAt;

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
