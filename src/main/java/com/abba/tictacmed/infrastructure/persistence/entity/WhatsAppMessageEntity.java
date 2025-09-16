package com.abba.tictacmed.infrastructure.persistence.entity;

import com.abba.tictacmed.domain.messaging.model.WhatsAppMessageStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(name = "whatsapp_messages")
@Data
public class WhatsAppMessageEntity {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "from_number", nullable = false, length = 50)
    private String from;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WhatsAppMessageStatus status;

    @Column(name = "received_at", nullable = false)
    private OffsetDateTime receivedAt;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    protected WhatsAppMessageEntity() {
    }

    public WhatsAppMessageEntity(String id, String from, String body, WhatsAppMessageStatus status, OffsetDateTime receivedAt, OffsetDateTime processedAt) {
        this.id = id;
        this.from = from;
        this.body = body;
        this.status = status;
        this.receivedAt = receivedAt;
        this.processedAt = processedAt;
    }
}