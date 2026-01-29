package com.abba.tanahora.infrastructure.config;

import com.whatsapp.api.WhatsappApiFactory;
import com.whatsapp.api.impl.WhatsappBusinessCloudApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WhatsAppClientConfig {

    private final WhatsAppProperties properties;

    @Bean
    public WhatsappBusinessCloudApi whatsappBusinessCloudApi() {
        return WhatsappApiFactory.newInstance(properties.getAccessToken())
                .newBusinessCloudApi();
    }
}
