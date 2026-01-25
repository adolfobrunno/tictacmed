package com.abba.tictacmed.application.service;

import com.abba.tictacmed.domain.model.Medication;
import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.ReminderStatus;
import com.abba.tictacmed.domain.model.User;
import com.abba.tictacmed.domain.repository.ReminderRepository;
import com.abba.tictacmed.domain.service.ReminderService;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class ReminderServiceImpl implements ReminderService {

    private static final ZoneId BRAZIL_ZONE = ZoneId.of("America/Sao_Paulo");

    private final ReminderRepository reminderRepository;

    public ReminderServiceImpl(ReminderRepository reminderRepository) {
        this.reminderRepository = reminderRepository;
    }

    @Override
    public Reminder scheduleMedication(User user, Medication med, String rrule) {

        if (rrule == null || rrule.isBlank()) {
            throw new IllegalArgumentException("rrule cannot be blank");
        }

        Reminder reminder = new Reminder();
        reminder.setMedication(med);
        reminder.setRrule(rrule);
        reminder.setStatus(ReminderStatus.ACTIVE);
        reminder.updateNextDispatch();
        return reminderRepository.save(reminder);
    }

    @Override
    public Optional<OffsetDateTime> getNextDispatch(Reminder reminder) {
        return Optional.ofNullable(reminder.getNextDispatch());
    }

    @Override
    public List<Reminder> getTodayPendingReminders() {
        return reminderRepository.findPendingNextDispatch(OffsetDateTime.now(BRAZIL_ZONE));
    }

    private OffsetDateTime buildNextDispatch(String scheduledAt) {
        OffsetDateTime now = OffsetDateTime.now(BRAZIL_ZONE);
        if (scheduledAt == null || scheduledAt.isBlank()) {
            return now;
        }
        LocalTime time = LocalTime.parse(scheduledAt);
        OffsetDateTime next = now.withHour(time.getHour()).withMinute(time.getMinute()).withSecond(0).withNano(0);
        if (!next.isAfter(now)) {
            next = next.plusDays(1);
        }
        return next;
    }

    private String buildDailyRrule(String scheduledAt) {
        if (scheduledAt == null || scheduledAt.isBlank()) {
            return "FREQ=DAILY";
        }
        LocalTime time = LocalTime.parse(scheduledAt);
        return "FREQ=DAILY;BYHOUR=" + time.getHour() + ";BYMINUTE=" + time.getMinute();
    }

    private boolean belongsToWhatsapp(Reminder reminder, String whatsappId) {
        if (whatsappId == null || whatsappId.isBlank()) {
            return false;
        }
        if (reminder.getMedication() == null || reminder.getMedication().getUser() == null) {
            return true;
        }
        return whatsappId.equals(reminder.getMedication().getUser().getWhatsappId());
    }
}
