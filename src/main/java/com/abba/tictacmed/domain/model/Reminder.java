package com.abba.tictacmed.domain.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "reminders")
@CompoundIndex(name = "dispatch_idx", def = "{'nextDispatch': 1, 'status': 1}")
@Data
public class Reminder {

    @Id
    private UUID id = UUID.randomUUID();
    private OffsetDateTime nextDispatch;
    private String rrule; // "FREQ=DAILY;BYHOUR=8"
    @Indexed(name = "status_idx")
    private ReminderStatus status = ReminderStatus.PENDING;

    @DBRef private Medication medication;

}
