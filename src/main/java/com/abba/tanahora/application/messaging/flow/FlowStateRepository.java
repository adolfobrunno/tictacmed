package com.abba.tanahora.application.messaging.flow;

public interface FlowStateRepository {

    FlowState load(String userId);

    void save(FlowState state);
}
