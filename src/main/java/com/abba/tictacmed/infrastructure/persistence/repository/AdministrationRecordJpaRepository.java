package com.abba.tictacmed.infrastructure.persistence.repository;

import com.abba.tictacmed.domain.scheduling.model.AdministrationStatus;
import com.abba.tictacmed.infrastructure.persistence.entity.AdministrationRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdministrationRecordJpaRepository extends JpaRepository<AdministrationRecordEntity, UUID> {
    List<AdministrationRecordEntity> findBySchedule_Id(UUID scheduleId);

    Optional<AdministrationRecordEntity> findTopOneBySchedule_Patient_IdAndStatusOrderByScheduledAtAsc(UUID patientId, AdministrationStatus status);
}
