package com.abba.tictacmed.infrastructure.mapper;

import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;
import com.abba.tictacmed.infrastructure.persistence.entity.AdministrationRecordEntity;
import com.abba.tictacmed.infrastructure.persistence.entity.MedicationScheduleEntity;
import com.abba.tictacmed.infrastructure.persistence.entity.PatientEntity;

import java.time.Duration;
import java.util.List;

public final class PersistenceMappers {
    private PersistenceMappers() {
    }

    public static PatientEntity toEntity(Patient patient) {
        return new PatientEntity(patient.getId(), patient.getName(), patient.getContact());
    }

    public static Patient toDomain(PatientEntity entity) {
        return Patient.fromExisting(entity.getId(), entity.getName(), entity.getContact());
    }

    public static MedicationScheduleEntity toEntity(MedicationSchedule schedule, PatientEntity patientEntity) {
        return new MedicationScheduleEntity(
                schedule.getId(),
                patientEntity,
                schedule.getMedicineName(),
                schedule.getStartAt(),
                schedule.getEndAt(),
                schedule.getFrequency().getSeconds()
        );
    }

    public static MedicationSchedule toDomain(MedicationScheduleEntity entity, Patient patient, List<AdministrationRecordEntity> records) {
        return new MedicationSchedule(
                entity.getId(),
                patient,
                entity.getMedicineName(),
                entity.getStartAt(),
                entity.getEndAt(),
                Duration.ofSeconds(entity.getFrequencySeconds()),
                records.stream().map(e -> new MedicationSchedule.AdministrationRecord(e.getScheduledAt(), e.getConfirmedAt(), e.getStatus())).toList()
        );
    }

}
