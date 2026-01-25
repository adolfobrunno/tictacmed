package com.abba.tictacmed.domain.repository;

import com.abba.tictacmed.domain.model.MessageReceived;
import com.abba.tictacmed.domain.model.MessageReceivedStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageReceivedRepository extends MongoRepository<MessageReceived, String> {

    List<MessageReceived> findAllByStatus(MessageReceivedStatus status);

    default List<MessageReceived> findAllPending() {
        return findAllByStatus(MessageReceivedStatus.PENDING);
    }

}
