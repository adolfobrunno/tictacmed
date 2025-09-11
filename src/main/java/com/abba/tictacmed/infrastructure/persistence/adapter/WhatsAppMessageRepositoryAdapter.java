package com.abba.tictacmed.infrastructure.persistence.adapter;

import com.abba.tictacmed.domain.messaging.model.WhatsAppMessage;
import com.abba.tictacmed.domain.messaging.model.WhatsAppMessageStatus;
import com.abba.tictacmed.domain.messaging.repository.WhatsAppMessageRepository;
import com.abba.tictacmed.infrastructure.persistence.entity.WhatsAppMessageEntity;
import com.abba.tictacmed.infrastructure.persistence.repository.WhatsAppMessageJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.stream.Collectors;

import static com.abba.tictacmed.infrastructure.mapper.PersistenceMappers.toOffset;
import static com.abba.tictacmed.infrastructure.mapper.PersistenceMappers.toZoned;

@Repository
public class WhatsAppMessageRepositoryAdapter implements WhatsAppMessageRepository {

    private final WhatsAppMessageJpaRepository jpaRepository;

    public WhatsAppMessageRepositoryAdapter(WhatsAppMessageJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(WhatsAppMessage message) {
        WhatsAppMessageEntity entity = new WhatsAppMessageEntity(
                message.getId(),
                message.getFrom(),
                message.getBody(),
                message.getStatus(),
                toOffset(message.getReceivedAt()),
                message.getProcessedAt() == null ? null : toOffset(message.getProcessedAt())
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
                e.getBody(),
                toZoned(e.getReceivedAt()),
                e.getProcessedAt() == null ? null : toZoned(e.getProcessedAt()),
                e.getStatus()
        );
    }
}
