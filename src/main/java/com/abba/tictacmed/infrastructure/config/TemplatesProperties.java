package com.abba.tictacmed.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "tictacmed.templates")
@Data
public class TemplatesProperties {

    private Map<String, String> values = new HashMap<>();

    public String getWelcomeMessageReceivedFree() {
        return values.get("welcome-message-received.free");
    }

    public String getWelcomeMessageSentFree() {
        return values.get("welcome-message-sent.free");
    }

    public String getWelcomeMessageReceivedPremium() {
        return values.get("welcome-message-received.premium");
    }

    public String getWelcomeMessageSentPremium() {
        return values.get("welcome-message-sent.premium");
    }


}
