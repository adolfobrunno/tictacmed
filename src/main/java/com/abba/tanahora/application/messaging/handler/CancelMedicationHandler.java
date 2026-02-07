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

import java.util.Optional;

@Component
@Slf4j
@Order(500)
public class CancelMedicationHandler implements HandleAndFlushMessageHandler {

    private final MessageClassifier messageClassifier;
    private final UserService userService;
    private final ReminderService reminderService;
    private final NotificationService notificationService;
    private final PatientResolverService patientResolverService;

    public CancelMedicationHandler(MessageClassifier messageClassifier, UserService userService, ReminderService reminderService, NotificationService notificationService, PatientResolverService patientResolverService) {
        this.messageClassifier = messageClassifier;
        this.userService = userService;
        this.reminderService = reminderService;
        this.notificationService = notificationService;
        this.patientResolverService = patientResolverService;
    }

    @Override
    public boolean supports(AIMessage message, FlowState state) {
        AiMessageProcessorDto dto = messageClassifier.classify(message, state);

        return dto.getType() == MessageReceivedType.REMINDER_CANCEL;
    }

    @Override
    public void handleAndFlush(AIMessage message, FlowState state) {

        AiMessageProcessorDto dto = messageClassifier.classify(message, state);

        String userId = state.getUserId();
        User user = userService.findByWhatsappId(userId);
        var patient = patientResolverService.resolve(user, dto.getPatientName(), state.getLastPatientId(), false);
        if (patient == null) {
            notificationService.sendNotification(user,
                    BasicWhatsAppMessage.builder().to(user.getWhatsappId()).message("Nao identifiquei o paciente. Informe o nome para cancelar.").build());
            return;
        }
        state.setLastPatientId(patient.getId());

        Optional<Reminder> reminderMatch = reminderService.getByUser(user)
                .stream()
                .filter(reminder -> patient.getId().equals(reminder.getPatientId()))
                .filter(reminder -> reminder.getMedication().getName().equalsIgnoreCase(dto.getMedication()))
                .findFirst();

        if (reminderMatch.isPresent()) {
            reminderService.cancelReminder(reminderMatch.get());
            notificationService.sendNotification(user, BasicWhatsAppMessage.builder().to(user.getWhatsappId()).message(reminderMatch.get().createCancelNotification()).build());
        } else {
            log.warn("Medication {} not found for user {}", dto.getMedication(), userId);
            notificationService.sendNotification(user, BasicWhatsAppMessage.builder().to(user.getWhatsappId()).message(String.format(
                    """
                    Ops! Parece que a medicação que você informou não está registrada.
                    
                    Confira se o nome "%s" está correto ou se você já havia cancelado essa medicação anteriormente.
                    
                    """, dto.getMedication())
            ).build());
        }
    }
}
