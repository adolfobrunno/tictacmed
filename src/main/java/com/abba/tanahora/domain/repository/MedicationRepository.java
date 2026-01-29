package com.abba.tanahora.domain.repository;

import com.abba.tanahora.domain.model.Medication;
import com.abba.tanahora.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface MedicationRepository extends MongoRepository<Medication, UUID> {

    List<Medication> findByUser(User user);
}
