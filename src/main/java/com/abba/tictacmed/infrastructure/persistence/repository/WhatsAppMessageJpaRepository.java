package com.abba.tictacmed.infrastructure.persistence.repository;

import com.abba.tictacmed.domain.messaging.model.WhatsAppMessageStatus;
import com.abba.tictacmed.infrastructure.persistence.entity.WhatsAppMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WhatsAppMessageJpaRepository extends JpaRepository<WhatsAppMessageEntity, String> {
    List<WhatsAppMessageEntity> findByStatus(WhatsAppMessageStatus status);
}