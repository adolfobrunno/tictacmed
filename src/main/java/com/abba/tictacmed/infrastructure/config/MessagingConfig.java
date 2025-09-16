package com.abba.tictacmed.infrastructure.config;

import com.abba.tictacmed.domain.messaging.service.NotificationSender;
import com.abba.tictacmed.domain.scheduling.service.SchedulingService;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.WhatsAppNotificationSender;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.WhatsAppProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({WhatsAppProperties.class})
public class MessagingConfig {

    @Bean
    public NotificationSender notificationSender(WhatsAppProperties props) {
        return new WhatsAppNotificationSender(props);
    }

    @Bean
    public SchedulingService schedulingService(NotificationSender sender) {
        return new SchedulingService(sender);
    }
}
