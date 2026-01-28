package com.abba.tictacmed.application.messaging.flow;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FlowState {

    private String userId;
    private String currentFlow;
    private String step;
    private Map<String, Object> context = new HashMap<>();
}
