package com.abba.tictacmed.domain.model;

import lombok.Data;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.RecurrenceSet;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recurrenceset.OfRule;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@Document(collection = "reminders")
@CompoundIndex(name = "dispatch_idx", def = "{'nextDispatch': 1, 'status': 1}")
@Data
public class Reminder {

    @Id
    private UUID id = UUID.randomUUID();
    private OffsetDateTime nextDispatch;
    private String rrule;
    @Indexed(name = "status_idx")
    private ReminderStatus status = ReminderStatus.ACTIVE;
    private OffsetDateTime createAt = OffsetDateTime.now();
    private OffsetDateTime canceledAt;


    @DBRef private Medication medication;

    public void updateNextDispatch() {

        ZoneId brazilZone = ZoneId.of("America/Sao_Paulo");

        try {
            RecurrenceRule rule = new RecurrenceRule(rrule);
            RecurrenceSet recurrenceInstances = new OfRule(rule, DateTime.now(TimeZone.getTimeZone(brazilZone)));
            DateTime next = recurrenceInstances.iterator().next();
            this.nextDispatch = OffsetDateTime.of(LocalDateTime.ofInstant(new Date(next.getTimestamp()).toInstant(),
                            OffsetDateTime.now().getOffset()),
                    OffsetDateTime.now().getOffset());
        } catch (InvalidRecurrenceRuleException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelReminder() {
        this.status = ReminderStatus.CANCELLED;
        this.canceledAt = OffsetDateTime.now();
    }

}
