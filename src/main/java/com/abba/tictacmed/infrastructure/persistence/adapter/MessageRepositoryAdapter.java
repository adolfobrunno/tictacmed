package com.abba.tictacmed.infrastructure.persistence.adapter;

import com.abba.tictacmed.domain.messaging.model.WhatsAppMessage;
import com.abba.tictacmed.domain.messaging.model.WhatsAppMessageStatus;
import com.abba.tictacmed.domain.messaging.repository.MessageRepository;
import com.abba.tictacmed.infrastructure.persistence.entity.WhatsAppMessageEntity;
import com.abba.tictacmed.infrastructure.persistence.repository.WhatsAppMessageJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class MessageRepositoryAdapter implements MessageRepository {

    private final WhatsAppMessageJpaRepository jpaRepository;

    public MessageRepositoryAdapter(WhatsAppMessageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(WhatsAppMessage message) {
        WhatsAppMessageEntity entity = new WhatsAppMessageEntity(
                message.getId(),
                message.getFrom(),
                message.getSenderName(),
                message.getBody(),
                message.getStatus(),
                message.getReceivedAt(),
                message.getProcessedAt() == null ? null : message.getProcessedAt()
        );
        jpaRepository.save(entity);
    }

    @Override
    public Set<WhatsAppMessage> findAllUnprocessed() {
        return jpaRepository.findByStatus(WhatsAppMessageStatus.UNPROCESSED)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toSet());
    }

    private WhatsAppMessage toDomain(WhatsAppMessageEntity e) {
        return WhatsAppMessage.fromExisting(
                e.getId(),
                e.getFrom(),
                e.getFromName(),
                e.getBody(),
                e.getReceivedAt(),
                e.getProcessedAt() == null ? null : e.getProcessedAt(),
                e.getStatus()
        );
    }
}
