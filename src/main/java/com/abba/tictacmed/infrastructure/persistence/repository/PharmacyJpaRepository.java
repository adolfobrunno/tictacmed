package com.abba.tictacmed.infrastructure.persistence.repository;

import com.abba.tictacmed.infrastructure.persistence.entity.PharmacyEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PharmacyJpaRepository extends MongoRepository<PharmacyEntity, String> {
}
