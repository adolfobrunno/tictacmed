package com.abba.tictacmed.infrastructure.config;

import com.abba.tictacmed.infrastructure.scheduling.SchedulerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties({SchedulerProperties.class})
public class SchedulerConfig {
}
