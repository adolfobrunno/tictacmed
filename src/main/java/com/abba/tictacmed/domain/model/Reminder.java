package com.abba.tictacmed.domain.model;

import com.abba.tictacmed.domain.exceptions.InvalidRruleException;
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
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.UUID;

import static com.abba.tictacmed.domain.utils.Constants.BRAZIL_ZONEID;

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
    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime canceledAt;

    @DBRef
    private User user;
    @DBRef private Medication medication;

    public void updateNextDispatch() {
        try {
            RecurrenceRule rule = new RecurrenceRule(rrule);
            TimeZone timeZone = TimeZone.getTimeZone(BRAZIL_ZONEID);
            DateTime now = DateTime.now(timeZone);
            DateTime start = createdAt == null
                    ? now
                    : new DateTime(timeZone, createdAt.toInstant().toEpochMilli());
            RecurrenceSet recurrenceInstances = new OfRule(rule, start);
            Iterator<DateTime> iterator = recurrenceInstances.iterator();
            if (!iterator.hasNext()) {
                this.nextDispatch = null;
                this.status = ReminderStatus.COMPLETED;
                return;
            }
            DateTime next = iterator.next();
            while (next.getTimestamp() <= now.getTimestamp()) {
                if (!iterator.hasNext()) {
                    this.nextDispatch = null;
                    this.status = ReminderStatus.COMPLETED;
                    return;
                }
                next = iterator.next();
            }
            this.nextDispatch = OffsetDateTime.of(
                    LocalDateTime.ofInstant(new Date(next.getTimestamp()).toInstant(), OffsetDateTime.now().getOffset()),
                    OffsetDateTime.now().getOffset());
        } catch (InvalidRecurrenceRuleException e) {
            throw new InvalidRruleException("Invalid RRULE find: " + rrule, e);
        }
    }

    public void cancelReminder() {
        this.status = ReminderStatus.CANCELLED;
        this.canceledAt = OffsetDateTime.now();
    }

    public boolean isActive() {
        return status == ReminderStatus.ACTIVE;
    }

    public String createSendReminderMessage() {
        return String.format("""
                Olá! 👋
                
                Está na hora de tomar seu medicamento: %s.
                
                Assim você mantém seu tratamento em dia!
                
                Responda como "Tomei" ✅ ou "Esqueci" ❌ para registrar.
                """, medication.getName());
    }

    public String createTakenConfirmationMessage() {
        return String.format("""
                ✅ Ótimo! Registramos que você tomou seu medicamento: %s.
                
                Continue assim!
                """, medication.getName());
    }

    public String createNextDispatchMessage() {
        return String.format("⏰ Próximo lembrete para o medicamento %s agendado para %s",
                medication.getName(),
                nextDispatch.atZoneSameInstant(BRAZIL_ZONEID).toLocalTime().toString());
    }

    public String createMissedReminderMessage() {
        return String.format("""
                ⚠️ Notamos que você não registrou a tomada do seu medicamento: %s.
                
                Lembre-se de manter seu tratamento em dia!
                
                Responda como "Tomei" ou "Esqueci" para registrar.
                """, medication.getName());
    }

    public String createSkippedConfirmationMessage() {
        return String.format("""
                ❌ Entendido. Registramos que você esqueceu de tomar seu medicamento: %s.
                
                Tente não esquecer da próxima vez!
                """, medication.getName());
    }

    public String createNewReminderMessage() {
        return String.format("""
                📅 Novo lembrete criado para o medicamento: %s.
                """, medication.getName());
    }

    public String createCompletedMessage() {
        return String.format("""
                🎉 Parabéns! Você concluiu o tratamento do seu medicamento: %s.
                """, medication.getName());
    }
}
