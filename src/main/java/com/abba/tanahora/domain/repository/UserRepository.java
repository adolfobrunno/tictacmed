package com.abba.tanahora.domain.repository;

import com.abba.tanahora.domain.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByWhatsappId(String whatsappId);

}
