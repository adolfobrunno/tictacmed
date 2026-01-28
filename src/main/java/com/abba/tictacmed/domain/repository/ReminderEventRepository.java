package com.abba.tictacmed.domain.repository;

import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.ReminderEvent;
import com.abba.tictacmed.domain.model.ReminderEventStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReminderEventRepository extends MongoRepository<ReminderEvent, UUID> {

    Optional<ReminderEvent> findFirstByWhatsappMessageId(String whatsappMessageId);

    Optional<ReminderEvent> findFirstByReminderAndStatusOrderBySentAtDesc(Reminder reminder, ReminderEventStatus status);
}
