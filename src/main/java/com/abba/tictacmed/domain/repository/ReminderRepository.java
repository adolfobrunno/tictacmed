package com.abba.tictacmed.domain.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.abba.tictacmed.domain.model.Reminder;

public interface ReminderRepository {

    Optional<Reminder> findById(UUID reminderId);
    List<Reminder> findPendingNextDispatch(OffsetDateTime now);
    void save(Reminder reminder);

}
