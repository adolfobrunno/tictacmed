package com.abba.tictacmed.application.messaging.handler;

import com.abba.tictacmed.application.dto.AiMessageProcessorDto;
import com.abba.tictacmed.application.dto.MessageReceivedType;
import com.abba.tictacmed.application.messaging.AIMessage;
import com.abba.tictacmed.application.messaging.classifier.MessageClassifier;
import com.abba.tictacmed.application.messaging.flow.FlowState;
import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.User;
import com.abba.tictacmed.domain.service.NotificationService;
import com.abba.tictacmed.domain.service.ReminderService;
import com.abba.tictacmed.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@Order(500)
public class CancelMedicationHandler implements MessageHandler {

    private final MessageClassifier messageClassifier;
    private final UserService userService;
    private final ReminderService reminderService;
    private final NotificationService notificationService;

    public CancelMedicationHandler(MessageClassifier messageClassifier, UserService userService, ReminderService reminderService, NotificationService notificationService) {
        this.messageClassifier = messageClassifier;
        this.userService = userService;
        this.reminderService = reminderService;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(AIMessage message, FlowState state) {
        AiMessageProcessorDto dto = messageClassifier.classify(message, state);

        return dto.getType() == MessageReceivedType.REMINDER_CANCEL;
    }

    @Override
    public void handle(AIMessage message, FlowState state) {

        AiMessageProcessorDto dto = messageClassifier.classify(message, state);

        String userId = state.getUserId();
        User user = userService.findByWhatsappId(userId);

        Optional<Reminder> reminderMatch = reminderService.getByUser(user)
                .stream()
                .filter(reminder -> reminder.getMedication().getName().equalsIgnoreCase(dto.getMedication()))
                .findFirst();

        if (reminderMatch.isPresent()) {
            reminderService.cancelReminder(reminderMatch.get());
            notificationService.sendNotification(user, reminderMatch.get().createCancelNotification());
        } else {
            log.warn("Medication {} not found for user {}", dto.getMedication(), userId);
            notificationService.sendNotification(user, String.format(
                    """
                            Ops! Parece que a medicação que você informou não está registrada.
                            
                            Confira se o nome "%s" está correto ou se você já havia cancelado essa medicação anteriormente.
                            
                            """, dto.getMedication()
            ));
        }

        state.setCurrentFlow(null);
        state.setStep(null);
        state.getContext().clear();
    }
}
