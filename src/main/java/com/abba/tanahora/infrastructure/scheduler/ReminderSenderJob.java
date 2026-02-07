package com.abba.tanahora.infrastructure.scheduler;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.abba.tanahora.application.notification.InteractiveWhatsAppMessage;
import com.abba.tanahora.domain.model.Reminder;
import com.abba.tanahora.domain.model.ReminderEvent;
import com.abba.tanahora.domain.service.NotificationService;
import com.abba.tanahora.domain.service.ReminderEventService;
import com.abba.tanahora.domain.service.ReminderService;
import com.whatsapp.api.domain.messages.Button;
import com.whatsapp.api.domain.messages.Reply;
import com.whatsapp.api.domain.messages.type.ButtonType;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "tictacmed.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ReminderSenderJob {

    private final ReminderService reminderService;
    private final ReminderEventService reminderEventService;
    private final NotificationService notificationService;


    public ReminderSenderJob(ReminderService reminderService, ReminderEventService reminderEventService, NotificationService notificationService) {
        this.reminderService = reminderService;
        this.reminderEventService = reminderEventService;
        this.notificationService = notificationService;
    }


    @Scheduled(fixedDelayString = "${tanahora.scheduler.send-reminders-delay-ms}")
    public void sendRemindNotification() {

        List<Reminder> reminders = reminderService.getNextsRemindersToNotify();

        reminders.forEach(reminder -> {
            if (reminder.isActive()) {
                Optional<ReminderEvent> pendingEvent = reminderEventService.findPendingByReminder(reminder);
                if (pendingEvent.isPresent()) {
                    String messageId = notificationService.sendNotification(reminder.getUser(), InteractiveWhatsAppMessage
                            .builder()
                            .to(reminder.getUser().getWhatsappId())
                            .text(reminder.createMissedReminderMessage())
                            .button(new Button().setType(ButtonType.REPLY).setReply(new Reply().setTitle("Tomei").setId("tomei_btn")))
                            .button(new Button().setType(ButtonType.REPLY).setReply(new Reply().setTitle("Esqueci").setId("esqueci_btn")))
                            .build());
                    reminderEventService.updateDispatch(pendingEvent.get(), messageId);
                } else {
                    String messageId = notificationService.sendNotification(reminder.getUser(), InteractiveWhatsAppMessage
                            .builder()
                            .to(reminder.getUser().getWhatsappId())
                            .text(reminder.createSendReminderMessage())
                            .button(new Button().setType(ButtonType.REPLY).setReply(new Reply().setTitle("Tomei").setId("tomei_btn")))
                            .button(new Button().setType(ButtonType.REPLY).setReply(new Reply().setTitle("Esqueci").setId("esqueci_btn")))
                            .build());
                    reminderEventService.registerDispatch(reminder, messageId);
                }
            } else {
                log.warn("Reminder {} is not active", reminder.getId());
            }
        });


    }

}
