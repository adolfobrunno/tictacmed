package com.abba.tictacmed.application.messaging.flow;

public interface FlowStateRepository {

    FlowState load(String userId);

    void save(FlowState state);
}
