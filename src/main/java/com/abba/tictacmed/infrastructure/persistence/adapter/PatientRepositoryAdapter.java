package com.abba.tictacmed.infrastructure.persistence.adapter;

import com.abba.tictacmed.domain.patient.model.Patient;
import com.abba.tictacmed.domain.patient.repository.PatientRepository;
import com.abba.tictacmed.infrastructure.mapper.PersistenceMappers;
import com.abba.tictacmed.infrastructure.persistence.entity.PatientEntity;
import com.abba.tictacmed.infrastructure.persistence.repository.PatientJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class PatientRepositoryAdapter implements PatientRepository {

    private final PatientJpaRepository jpaRepository;

    public PatientRepositoryAdapter(PatientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    @Transactional
    public Patient save(Patient patient) {
        PatientEntity entity = PersistenceMappers.toEntity(patient);
        PatientEntity saved = jpaRepository.save(entity);
        return PersistenceMappers.toDomain(saved);
    }

    @Override
    public Optional<Patient> findById(String id) {
        return jpaRepository.findById(id).map(PersistenceMappers::toDomain);
    }

    @Override
    public Optional<Patient> findByContact(String contact) {
        return jpaRepository.findByContact(contact).map(PersistenceMappers::toDomain);
    }
}
