package com.abba.tanahora.infrastructure.scheduler;

import com.abba.tanahora.domain.model.Reminder;
import com.abba.tanahora.domain.model.ReminderEvent;
import com.abba.tanahora.domain.service.NotificationService;
import com.abba.tanahora.domain.service.ReminderEventService;
import com.abba.tanahora.domain.service.ReminderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
                        notificationService.sendNotification(reminder.getUser(), reminder.createMissedReminderMessage());
                } else {
                    String messageId = notificationService.sendNotification(reminder.getUser(), reminder.createSendReminderMessage());
                    reminderEventService.registerDispatch(reminder, messageId);
                }
            } else {
                log.warn("Reminder {} is not active", reminder.getId());
            }
        });


    }

}
