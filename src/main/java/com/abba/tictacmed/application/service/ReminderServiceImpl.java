package com.abba.tictacmed.application.service;

import com.abba.tictacmed.domain.model.Medication;
import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.ReminderStatus;
import com.abba.tictacmed.domain.model.User;
import com.abba.tictacmed.domain.repository.ReminderRepository;
import com.abba.tictacmed.domain.service.ReminderService;
import org.springframework.stereotype.Service;

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

    @Override
    public void cancelReminder(Reminder reminder) {
        reminder.cancelReminder();
        reminderRepository.save(reminder);
    }
}
