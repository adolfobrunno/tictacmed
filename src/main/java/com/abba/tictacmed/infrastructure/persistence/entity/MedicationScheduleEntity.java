package com.abba.tictacmed.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Document(collection = "medication_schedule")
@AllArgsConstructor
@NoArgsConstructor
public class MedicationScheduleEntity {

    @Id
    private UUID id;

    @DBRef
    private PatientEntity patient;

    private String medicineName;

    private OffsetDateTime startAt;

    private OffsetDateTime endAt;

    private long frequencySeconds;

    private boolean recurring;

    private boolean active;

}
