package com.abba.tanahora.application.service;

import com.abba.tanahora.application.dto.MessageReceivedType;
import com.abba.tanahora.domain.model.Reminder;
import com.abba.tanahora.domain.model.ReminderEvent;
import com.abba.tanahora.domain.model.ReminderEventStatus;
import com.abba.tanahora.domain.model.User;
import com.abba.tanahora.domain.repository.ReminderEventRepository;
import com.abba.tanahora.domain.service.ReminderEventService;
import com.abba.tanahora.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Slf4j
public class ReminderEventServiceImpl implements ReminderEventService {

    private final ReminderEventRepository reminderEventRepository;
    private final UserService userService;

    public ReminderEventServiceImpl(ReminderEventRepository reminderEventRepository, UserService userService) {
        this.reminderEventRepository = reminderEventRepository;
        this.userService = userService;
    }

    @Override
    public ReminderEvent registerDispatch(Reminder reminder, String whatsappMessageId) {
        ReminderEvent event = new ReminderEvent();
        event.setReminder(reminder);
        event.setWhatsappMessageId(whatsappMessageId);
        event.setUserWhatsappId(reminder.getUser().getWhatsappId());
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

        log.debug("Updating reminder event status for replyToMessageId={} responseText={} userId={}", replyToMessageId, responseText, userId);

        if(replyToMessageId == null) {
            event = reminderEventRepository.findLastByUserWhatsappIdAndStatus(user.getWhatsappId(), ReminderEventStatus.PENDING);
        } else {
            event = reminderEventRepository.findFirstByWhatsappMessageId(replyToMessageId);
        }

        ReminderEventStatus reminderEventStatus = resolveStatus(responseText);

        log.debug("Updating reminder event {} status to {}", event, reminderEventStatus);

        event.ifPresent(e -> {
            e.setStatus(reminderEventStatus);
            e.setResponseReceivedAt(OffsetDateTime.now());
            reminderEventRepository.save(e);
        });


        return event;
    }

    private ReminderEventStatus resolveStatus(String responseText) {

        MessageReceivedType messageReceivedType = MessageReceivedType.valueOf(responseText);

        return messageReceivedType == MessageReceivedType.REMINDER_RESPONSE_TAKEN ? ReminderEventStatus.TAKEN : ReminderEventStatus.SKIPPED;
    }
}
