package com.abba.tictacmed.application.messaging.flow;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryFlowStateRepository implements FlowStateRepository {

    private final Map<String, FlowState> states = new ConcurrentHashMap<>();

    @Override
    public FlowState load(String userId) {
        if (userId == null || userId.isBlank()) {
            FlowState state = new FlowState();
            state.setUserId("anonymous");
            return state;
        }
        return states.computeIfAbsent(userId, id -> {
            FlowState state = new FlowState();
            state.setUserId(id);
            return state;
        });
    }

    @Override
    public void save(FlowState state) {
        if (state == null || state.getUserId() == null || state.getUserId().isBlank()) {
            return;
        }
        states.put(state.getUserId(), state);
    }
}
