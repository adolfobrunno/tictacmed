package com.abba.tictacmed.infrastructure.persistence.repository;

import com.abba.tictacmed.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserJpaRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByUsername(String username);
}
