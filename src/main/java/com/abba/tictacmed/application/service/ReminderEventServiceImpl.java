package com.abba.tictacmed.application.service;

import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.ReminderEvent;
import com.abba.tictacmed.domain.model.ReminderEventStatus;
import com.abba.tictacmed.domain.repository.ReminderEventRepository;
import com.abba.tictacmed.domain.service.ReminderEventService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class ReminderEventServiceImpl implements ReminderEventService {

    private final ReminderEventRepository reminderEventRepository;

    public ReminderEventServiceImpl(ReminderEventRepository reminderEventRepository) {
        this.reminderEventRepository = reminderEventRepository;
    }

    @Override
    public ReminderEvent registerDispatch(Reminder reminder, String whatsappMessageId) {
        ReminderEvent event = new ReminderEvent();
        event.setReminder(reminder);
        event.setWhatsappMessageId(whatsappMessageId);
        return reminderEventRepository.save(event);
    }

    @Override
    public Optional<ReminderEvent> updateStatusFromResponse(String replyToMessageId, String responseText) {
        if (replyToMessageId == null || replyToMessageId.isBlank()) {
            return Optional.empty();
        }
        Optional<ReminderEventStatus> status = parseStatus(responseText);
        return status.flatMap(reminderEventStatus -> reminderEventRepository.findFirstByWhatsappMessageId(replyToMessageId)
                .filter(event -> event.getStatus() == ReminderEventStatus.PENDING)
                .map(event -> {
                    event.setStatus(reminderEventStatus);
                    event.setResponseReceivedAt(OffsetDateTime.now());
                    return reminderEventRepository.save(event);
                }));
    }

    private Optional<ReminderEventStatus> parseStatus(String responseText) {
        if (responseText == null || responseText.isBlank()) {
            return Optional.empty();
        }
        String normalized = responseText.trim().toUpperCase();
        if ("TAKEN".equals(normalized)) {
            return Optional.of(ReminderEventStatus.TAKEN);
        }
        if ("SKIPPED".equals(normalized)) {
            return Optional.of(ReminderEventStatus.SKIPPED);
        }
        return Optional.empty();
    }
}
