package com.abba.tictacmed.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tictacmed.whatsapp")
@Data
public class WhatsAppProperties {

    private boolean enabled;
    private String fromNumber;
    private String template;
    private String verifyToken;
    private String accessToken;
}
