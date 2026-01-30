package com.abba.tanahora.application.service;

import com.abba.tanahora.domain.model.Reminder;
import com.abba.tanahora.domain.model.ReminderEvent;
import com.abba.tanahora.domain.model.ReminderEventStatus;
import com.abba.tanahora.domain.model.User;
import com.abba.tanahora.domain.repository.ReminderEventRepository;
import com.abba.tanahora.domain.service.ReminderEventService;
import com.abba.tanahora.domain.service.ReminderService;
import com.abba.tanahora.domain.service.UserService;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class ReminderEventServiceImpl implements ReminderEventService {

    private final ReminderEventRepository reminderEventRepository;
    private final OpenAiApiService openAiApiService;
    private final ReminderService reminderService;
    private final UserService userService;

    public ReminderEventServiceImpl(ReminderEventRepository reminderEventRepository, OpenAiApiService openAiApiService, ReminderService reminderService, UserService userService) {
        this.reminderEventRepository = reminderEventRepository;
        this.openAiApiService = openAiApiService;
        this.reminderService = reminderService;
        this.userService = userService;
    }

    @Override
    public ReminderEvent registerDispatch(Reminder reminder, String whatsappMessageId) {
        ReminderEvent event = new ReminderEvent();
        event.setReminder(reminder);
        event.setWhatsappMessageId(whatsappMessageId);
        return reminderEventRepository.save(event);
    }

    @Override
    public Optional<ReminderEvent> findPendingByReminder(Reminder reminder) {
        return reminderEventRepository.findFirstByReminderAndStatusOrderBySentAtDesc(reminder, ReminderEventStatus.PENDING);
    }

    @Override
    public ReminderEvent updateDispatch(ReminderEvent event, String whatsappMessageId) {
        event.setWhatsappMessageId(whatsappMessageId);
        event.setSentAt(OffsetDateTime.now());
        return reminderEventRepository.save(event);
    }

    @Override
    public Optional<ReminderEvent> updateStatusFromResponse(String replyToMessageId, String responseText, String userId) {

        User user = userService.findByWhatsappId(userId);
        Optional<ReminderEvent> event;

        if(replyToMessageId == null) {
            event = reminderEventRepository.findLastByReminderUserAndStatus(user, ReminderEventStatus.PENDING);
        } else {
            event = reminderEventRepository.findFirstByWhatsappMessageId(replyToMessageId);
        }

        ReminderEventStatus reminderEventStatus = ReminderEventStatus.valueOf(responseText);

        event.ifPresent(e -> {
            e.setStatus(reminderEventStatus);
            e.setResponseReceivedAt(OffsetDateTime.now());
            reminderEventRepository.save(e);
        });


        return event;
    }

    static class ReminderEventStatusDTO {
        @JsonProperty(required = true)
        ReminderEventStatus status;
    }
}
