package com.abba.tictacmed.domain.repository;

import com.abba.tictacmed.domain.model.Reminder;
import com.abba.tictacmed.domain.model.ReminderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface ReminderRepository extends MongoRepository<Reminder, UUID> {

    List<Reminder> findByNextDispatchLessThanEqualAndStatus(OffsetDateTime now, ReminderStatus status);

    default List<Reminder> findPendingNextDispatch(OffsetDateTime now) {
        return findByNextDispatchLessThanEqualAndStatus(now, ReminderStatus.ACTIVE);
    }

}
