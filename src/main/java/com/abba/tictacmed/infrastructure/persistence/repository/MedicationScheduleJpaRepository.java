package com.abba.tictacmed.infrastructure.persistence.repository;

import com.abba.tictacmed.infrastructure.persistence.entity.MedicationScheduleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface MedicationScheduleJpaRepository extends MongoRepository<MedicationScheduleEntity, UUID> {
    List<MedicationScheduleEntity> findByPatient_Id(UUID patientId);

    List<MedicationScheduleEntity> findAll();
}
