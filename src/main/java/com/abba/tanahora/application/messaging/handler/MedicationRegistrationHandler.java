package com.abba.tanahora.application.messaging.handler;

import com.abba.tanahora.application.dto.AiMessageProcessorDto;
import com.abba.tanahora.application.dto.MessageReceivedType;
import com.abba.tanahora.application.exceptions.ReminderLimitException;
import com.abba.tanahora.application.messaging.AIMessage;
import com.abba.tanahora.application.messaging.classifier.MessageClassifier;
import com.abba.tanahora.application.messaging.flow.FlowState;
import com.abba.tanahora.domain.model.Medication;
import com.abba.tanahora.domain.model.Reminder;
import com.abba.tanahora.domain.model.User;
import com.abba.tanahora.domain.service.MedicationService;
import com.abba.tanahora.domain.service.NotificationService;
import com.abba.tanahora.domain.service.ReminderService;
import com.abba.tanahora.domain.service.UserService;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(200)
public class MedicationRegistrationHandler implements MessageHandler {

    private final MessageClassifier messageClassifier;
    private final MedicationService medicationService;
    private final UserService userService;
    private final ReminderService reminderService;
    private final NotificationService notificationService;

    public MedicationRegistrationHandler(MessageClassifier messageClassifier,
                                         MedicationService medicationService,
                                         UserService userService,
                                         ReminderService reminderService,
                                         NotificationService notificationService) {
        this.messageClassifier = messageClassifier;
        this.medicationService = medicationService;
        this.userService = userService;
        this.reminderService = reminderService;
        this.notificationService = notificationService;
    }

    @Override
    public boolean supports(AIMessage message, FlowState state) {
        AiMessageProcessorDto dto = messageClassifier.classify(message, state);
        return dto != null && dto.getType() == MessageReceivedType.REMINDER_CREATION;
    }

    @Override
    public void handle(AIMessage message, FlowState state) {
        AiMessageProcessorDto dto = messageClassifier.classify(message, state);
        if (dto == null || dto.getType() != MessageReceivedType.REMINDER_CREATION) {
            return;
        }
        if (dto.getMedication() == null || dto.getMedication().isBlank()) {
            return;
        }
        if (dto.getRrule() == null || dto.getRrule().isBlank()) {
            return;
        }

        User user = userService.register(message.getWhatsappId(), message.getContactName());
        Medication medication = medicationService.createMedication(user, dto.getMedication());
        try {
            Reminder reminder = reminderService.scheduleMedication(user, medication, dto.getRrule());
            notificationService.sendNotification(user, reminder.createNewReminderMessage());
        } catch (ReminderLimitException e) {
            notificationService.sendNotification(user,
                    """
                            Ops! 🙁 Você chegou ao limite de lembretes do plano gratuito.
                            Para criar novos lembretes, faça o upgrade para o plano Premium.
                            Se quiser, é só responder esta mensagem e eu te envio o link para upgrade.
                            
                            Se preferir, você também pode apagar um lembrete existente e cadastrar um novo no lugar.
                            """);
        }
    }
}
