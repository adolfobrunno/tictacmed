package com.abba.tictacmed.infrastructure.messaging.whatsapp;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tictacmed.whatsapp")
@Getter
@Setter
public class WhatsAppProperties {
    private boolean enabled;
    private String fromNumber;
    private String template;
    private String verifyToken;
    private String accessToken;


}