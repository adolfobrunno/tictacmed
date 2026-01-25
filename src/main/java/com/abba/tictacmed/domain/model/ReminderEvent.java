package com.abba.tictacmed.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.UUID;

@Document(collection = "reminder_events")
@Data
public class ReminderEvent {

    @Id
    private UUID id = UUID.randomUUID();
    private OffsetDateTime sentAt = OffsetDateTime.now();
    private OffsetDateTime responseReceivedAt;

    @Indexed(name = "reminder_event_message_idx")
    private String whatsappMessageId;

    private ReminderEventStatus status = ReminderEventStatus.PENDING;

    @DBRef
    private Reminder reminder;

}
