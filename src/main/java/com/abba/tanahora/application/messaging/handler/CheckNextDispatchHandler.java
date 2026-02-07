package com.abba.tanahora.application.messaging.handler;

import com.abba.tanahora.application.dto.AiMessageProcessorDto;
import com.abba.tanahora.application.dto.MessageReceivedType;
import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.classifier.MessageClassifier;
import com.abba.tanahora.application.messaging.flow.FlowState;
import com.abba.tanahora.application.notification.BasicWhatsAppMessage;
import com.abba.tanahora.domain.model.Reminder;
import com.abba.tanahora.domain.model.User;
import com.abba.tanahora.domain.service.NotificationService;
import com.abba.tanahora.domain.service.PatientResolverService;
import com.abba.tanahora.domain.service.ReminderService;
import com.abba.tanahora.domain.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Comparator;

@Component
@Slf4j
@Order(300)
public class CheckNextDispatchHandler implements HandleAndFlushMessageHandler {

    private final MessageClassifier messageClassifier;
    private final ReminderService reminderService;
    private final UserService userService;
    private final NotificationService notificationService;
    private final PatientResolverService patientResolverService;

    public CheckNextDispatchHandler(MessageClassifier messageClassifier, ReminderService reminderService, UserService userService, NotificationService notificationService, PatientResolverService patientResolverService) {
        this.messageClassifier = messageClassifier;
        this.reminderService = reminderService;
        this.userService = userService;
        this.notificationService = notificationService;
        this.patientResolverService = patientResolverService;
    }


    @Override
    public boolean supports(AIMessage message, FlowState state) {
        AiMessageProcessorDto dto = messageClassifier.classify(message, state);
        return dto.getType() == MessageReceivedType.CHECK_NEXT_DISPATCH;
    }

    @Override
    public void handleAndFlush(AIMessage message, FlowState state) {
        log.info("Checking next dispatch for user={}", state.getUserId());
        User user = userService.findByWhatsappId(state.getUserId());

        reminderService.getByUser(user)
                .stream()
                .min(Comparator.comparing(Reminder::getNextDispatch))
                .ifPresentOrElse(
                        reminder -> notificationService.sendNotification(user, BasicWhatsAppMessage.builder()
                                .to(user.getWhatsappId())
                                .message(reminder.createNextDispatchMessage())
                                .build()),
                        () -> notificationService.sendNotification(user, BasicWhatsAppMessage.builder()
                                .to(user.getWhatsappId())
                                .message("""
                                        Você não tem nenhum medicamento agendado.
                                        
                                        Que tal começar registrando um agora mesmo?
                                        """)
                                .build()));


    }
}
