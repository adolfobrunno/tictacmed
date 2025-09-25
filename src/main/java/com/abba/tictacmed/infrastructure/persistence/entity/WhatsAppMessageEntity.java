package com.abba.tictacmed.infrastructure.persistence.entity;

import com.abba.tictacmed.domain.messaging.model.WhatsAppMessageStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;

@Data
@Document(collection = "whatsapp_messages")
public class WhatsAppMessageEntity {

    @Id
    private String id;

    private String from;

    private String fromName;

    private String body;

    private WhatsAppMessageStatus status;

    private OffsetDateTime receivedAt;

    private OffsetDateTime processedAt;

    protected WhatsAppMessageEntity() {
    }

    public WhatsAppMessageEntity(String id, String from, String fromName, String body, WhatsAppMessageStatus status, OffsetDateTime receivedAt, OffsetDateTime processedAt) {
        this.id = id;
        this.from = from;
        this.fromName = fromName;
        this.body = body;
        this.status = status;
        this.receivedAt = receivedAt;
        this.processedAt = processedAt;
    }
}