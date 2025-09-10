package com.abba.tictacmed.infrastructure.persistence.adapter;

import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.scheduling.model.MedicationSchedule;
import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import com.abba.tictacmed.infrastructure.mapper.PersistenceMappers;
import com.abba.tictacmed.infrastructure.persistence.entity.AdministrationRecordEntity;
import com.abba.tictacmed.infrastructure.persistence.entity.MedicationScheduleEntity;
import com.abba.tictacmed.infrastructure.persistence.entity.PatientEntity;
import com.abba.tictacmed.infrastructure.persistence.repository.AdministrationRecordJpaRepository;
import com.abba.tictacmed.infrastructure.persistence.repository.MedicationScheduleJpaRepository;
import com.abba.tictacmed.infrastructure.persistence.repository.PatientJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MedicationScheduleRepositoryAdapter implements MedicationScheduleRepository {

    private final MedicationScheduleJpaRepository scheduleRepo;
    private final AdministrationRecordJpaRepository recordRepo;
    private final PatientJpaRepository patientRepo;

    public MedicationScheduleRepositoryAdapter(MedicationScheduleJpaRepository scheduleRepo,
                                               AdministrationRecordJpaRepository recordRepo,
                                               PatientJpaRepository patientRepo) {
        this.scheduleRepo = scheduleRepo;
        this.recordRepo = recordRepo;
        this.patientRepo = patientRepo;
    }

    @Override
    @Transactional
    public MedicationSchedule save(MedicationSchedule schedule) {
        PatientEntity patientEntity = patientRepo.findById(schedule.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found: " + schedule.getPatient().getId()));
        MedicationScheduleEntity entity = PersistenceMappers.toEntity(schedule, patientEntity);
        MedicationScheduleEntity saved = scheduleRepo.save(entity);
        // Simple approach: clear and re-save administration records for this schedule idempotently
        List<AdministrationRecordEntity> existing = recordRepo.findBySchedule_Id(saved.getId());
        recordRepo.deleteAll(existing);
        schedule.getAdministrations().forEach(ar -> {
            AdministrationRecordEntity rec = new AdministrationRecordEntity(
                    UUID.randomUUID(), saved,
                    PersistenceMappers.toOffset(ar.scheduledAt()),
                    PersistenceMappers.toOffset(ar.confirmedAt())
            );
            recordRepo.save(rec);
        });
        List<AdministrationRecordEntity> records = recordRepo.findBySchedule_Id(saved.getId());
        Patient patient = PersistenceMappers.toDomain(patientEntity);
        return PersistenceMappers.toDomain(saved, patient, records);
    }

    @Override
    public Optional<MedicationSchedule> findById(UUID id) {
        return scheduleRepo.findById(id).map(entity -> {
            PatientEntity peProxy = entity.getPatient();
            PatientEntity pe = patientRepo.findById(peProxy.getId()).orElseThrow();
            Patient patient = PersistenceMappers.toDomain(pe);
            List<AdministrationRecordEntity> records = recordRepo.findBySchedule_Id(entity.getId());
            return PersistenceMappers.toDomain(entity, patient, records);
        });
    }

    @Override
    public List<MedicationSchedule> findByPatientId(UUID patientId) {
        return scheduleRepo.findByPatient_Id(patientId).stream().map(entity -> {
            PatientEntity peProxy = entity.getPatient();
            PatientEntity pe = patientRepo.findById(peProxy.getId()).orElseThrow();
            Patient patient = PersistenceMappers.toDomain(pe);
            List<AdministrationRecordEntity> records = recordRepo.findBySchedule_Id(entity.getId());
            return PersistenceMappers.toDomain(entity, patient, records);
        }).toList();
    }

    @Override
    public List<MedicationSchedule> findAll() {
        return scheduleRepo.findAll().stream().map(entity -> {
            PatientEntity peProxy = entity.getPatient();
            PatientEntity pe = patientRepo.findById(peProxy.getId()).orElseThrow();
            Patient patient = PersistenceMappers.toDomain(pe);
            List<AdministrationRecordEntity> records = recordRepo.findBySchedule_Id(entity.getId());
            return PersistenceMappers.toDomain(entity, patient, records);
        }).toList();
    }
}
