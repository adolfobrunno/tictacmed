package com.abba.tanahora.application.messaging.handler;

import com.abba.tanahora.application.dto.AiMessageProcessorDto;
import com.abba.tanahora.application.dto.MessageReceivedType;
import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.classifier.MessageClassifier;
import com.abba.tanahora.application.messaging.flow.FlowState;
import com.abba.tanahora.domain.model.Reminder;
import com.abba.tanahora.domain.model.ReminderEvent;
import com.abba.tanahora.domain.service.NotificationService;
import com.abba.tanahora.domain.service.ReminderEventService;
import com.abba.tanahora.domain.service.ReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@Order(400)
public class ReplyReminderEventHandler implements MessageHandler {

    private final MessageClassifier messageClassifier;
    private final ReminderEventService reminderEventService;
    private final NotificationService notificationService;
    private final ReminderService reminderService;

    public ReplyReminderEventHandler(MessageClassifier messageClassifier, ReminderEventService reminderEventService, NotificationService notificationService, ReminderService reminderService) {
        this.messageClassifier = messageClassifier;
        this.reminderEventService = reminderEventService;
        this.notificationService = notificationService;
        this.reminderService = reminderService;
    }

    @Override
    public boolean supports(AIMessage message, FlowState state) {
        AiMessageProcessorDto dto = messageClassifier.classify(message, state);
        return dto.getType() == MessageReceivedType.REMINDER_RESPONSE_TAKEN ||
                dto.getType() == MessageReceivedType.REMINDER_RESPONSE_SKIPPED;
    }

    @Override
    public void handle(AIMessage message, FlowState state) {
        log.info("Updating reminder event status for message id={} whatsappId={}", message.getId(), message.getWhatsappId());
        AiMessageProcessorDto dto = messageClassifier.classify(message, state);
        Optional<ReminderEvent> reminderEvent = reminderEventService.updateLastPending(state.getUserId(), dto.getType().name());
        reminderEvent.ifPresent(event -> {
            Reminder reminder = event.getReminder();
            String messageToResponse = dto.getType() == MessageReceivedType.REMINDER_RESPONSE_TAKEN ?
                    reminder.createTakenConfirmationMessage() : reminder.createSkippedConfirmationMessage();
            notificationService.sendNotification(reminder.getUser(), messageToResponse);
            reminderService.updateReminderNextDispatch(reminder);
            state.setCurrentFlow(null);
            state.setStep(null);
            state.getContext().clear();
        });
    }
}
