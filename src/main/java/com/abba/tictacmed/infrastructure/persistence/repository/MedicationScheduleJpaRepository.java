package com.abba.tictacmed.infrastructure.persistence.repository;

import com.abba.tictacmed.infrastructure.persistence.entity.MedicationScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicationScheduleJpaRepository extends JpaRepository<MedicationScheduleEntity, UUID> {
    List<MedicationScheduleEntity> findByPatient_Id(UUID patientId);

    List<MedicationScheduleEntity> findAll();
}
