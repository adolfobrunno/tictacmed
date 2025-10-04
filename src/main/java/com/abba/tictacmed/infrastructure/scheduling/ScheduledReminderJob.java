package com.abba.tictacmed.infrastructure.scheduling;

import com.abba.tictacmed.domain.scheduling.repository.MedicationScheduleRepository;
import com.abba.tictacmed.domain.scheduling.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ScheduledReminderJob {
    private static final Logger log = LoggerFactory.getLogger(ScheduledReminderJob.class);

    private final MedicationScheduleRepository scheduleRepository;
    private final SchedulingService schedulingService;
    private final SchedulerProperties properties;

    /**
     * Periodically scans schedules and sends WhatsApp reminders for the next due slot,
     * but only for those with next dose within the next 30 minutes.
     */
    @Scheduled(fixedDelayString = "${tictacmed.scheduler.fixed-delay-ms:60000}")
    public void run() {
        if (!properties.isEnabled()) {
            log.debug("Reminder scheduler disabled; skipping run");
            return;
        }
        var now = OffsetDateTime.now();
        var upperBound = now.plusMinutes(30);
        int notifications = 0;
        for (var schedule : scheduleRepository.findAll()) {
            var nextDue = schedule.nextDue(now);
            if (nextDue.isPresent()) {
                var dueAt = nextDue.get();
                if ((dueAt.isEqual(now) || dueAt.isAfter(now)) && (dueAt.isBefore(upperBound) || dueAt.isEqual(upperBound))) {
                    var notified = schedulingService.notifyNextDue(schedule, now);
                    if (notified != null) notifications++;
                }
            }
        }
        if (notifications > 0) {
            log.info("Reminder job sent {} notifications", notifications);
        } else {
            log.debug("Reminder job found no due reminders in the next 30 minutes");
        }
    }
}
