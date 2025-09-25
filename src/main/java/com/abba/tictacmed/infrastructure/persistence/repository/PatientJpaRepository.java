package com.abba.tictacmed.infrastructure.persistence.repository;

import com.abba.tictacmed.infrastructure.persistence.entity.PatientEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PatientJpaRepository extends MongoRepository<PatientEntity, String> {

    Optional<PatientEntity> findByContact(String contact);

}
