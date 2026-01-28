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

import java.util.Comparator;

@Component
@Slf4j
@Order(300)
public class CheckNextDispatchHandler implements MessageHandler {

    private final MessageClassifier messageClassifier;
    private final ReminderService reminderService;
    private final UserService userService;
    private final NotificationService notificationService;

    public CheckNextDispatchHandler(MessageClassifier messageClassifier, ReminderService reminderService, UserService userService, NotificationService notificationService) {
        this.messageClassifier = messageClassifier;
        this.reminderService = reminderService;
        this.userService = userService;
        this.notificationService = notificationService;
    }


    @Override
    public boolean supports(AIMessage message, FlowState state) {
        AiMessageProcessorDto dto = messageClassifier.classify(message, state);
        return dto.getType() == MessageReceivedType.CHECK_NEXT_DISPATCH;
    }

    @Override
    public void handle(AIMessage message, FlowState state) {
        log.info("Checking next dispatch for user={}", state.getUserId());
        User user = userService.findByWhatsappId(state.getUserId());

        reminderService.getByUser(user)
                .stream()
                .min(Comparator.comparing(Reminder::getNextDispatch))
                .ifPresentOrElse(reminder -> notificationService.sendNotification(user, reminder.createNextDispatchMessage()),
                        () -> notificationService.sendNotification(user, "Você não tem nenhum medicamento agendado"));


    }
}
