package com.abba.tictacmed.infrastructure.scheduling;

import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import com.abba.tictacmed.domain.scheduling.service.SchedulingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

@Component
public class ScheduledReminderJob {
    private static final Logger log = LoggerFactory.getLogger(ScheduledReminderJob.class);

    private final MedicationScheduleRepository scheduleRepository;
    private final SchedulingService schedulingService;
    private final SchedulerProperties properties;

    public ScheduledReminderJob(MedicationScheduleRepository scheduleRepository,
                                SchedulingService schedulingService,
                                SchedulerProperties properties) {
        this.scheduleRepository = scheduleRepository;
        this.schedulingService = schedulingService;
        this.properties = properties;
    }

    /**
     * Periodically scans schedules and sends WhatsApp reminders for the next due slot.
     * Minimal implementation: iterates all schedules. For production, narrow by time window.
     */
    @Scheduled(fixedDelayString = "${tictacmed.scheduler.fixed-delay-ms:60000}")
    public void run() {
        if (!properties.isEnabled()) {
            log.debug("Reminder scheduler disabled; skipping run");
            return;
        }
        var now = ZonedDateTime.now();
        int notifications = 0;
        for (var schedule : scheduleRepository.findAll()) {
            var notified = schedulingService.notifyNextDue(schedule, now);
            if (notified != null) notifications++;
        }
        if (notifications > 0) {
            log.info("Reminder job sent {} notifications", notifications);
        } else {
            log.debug("Reminder job found no due reminders");
        }
    }

    // Expose a method to facilitate tests without waiting on @Scheduled timing.
    public int runOnceNow() {
        if (!properties.isEnabled()) return 0;
        var now = ZonedDateTime.now();
        int notifications = 0;
        for (var schedule : scheduleRepository.findAll()) {
            var notified = schedulingService.notifyNextDue(schedule, now);
            if (notified != null) notifications++;
        }
        return notifications;
    }
}
