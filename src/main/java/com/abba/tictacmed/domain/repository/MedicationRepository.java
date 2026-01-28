package com.abba.tictacmed.domain.repository;

import com.abba.tictacmed.domain.model.Medication;
import com.abba.tictacmed.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface MedicationRepository extends MongoRepository<Medication, UUID> {

    List<Medication> findByUser(User user);
}
