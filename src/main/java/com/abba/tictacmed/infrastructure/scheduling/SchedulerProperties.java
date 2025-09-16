package com.abba.tictacmed.infrastructure.scheduling;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tictacmed.scheduler")
public class SchedulerProperties {
    /**
     * Enable reminder scheduler job.
     */
    private boolean enabled = true;
    /**
     * Fixed delay between scans in milliseconds. Default: 60000 (1 minute).
     */
    private long fixedDelayMs = 60_000L;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public long getFixedDelayMs() {
        return fixedDelayMs;
    }

    public void setFixedDelayMs(long fixedDelayMs) {
        this.fixedDelayMs = fixedDelayMs;
    }
}