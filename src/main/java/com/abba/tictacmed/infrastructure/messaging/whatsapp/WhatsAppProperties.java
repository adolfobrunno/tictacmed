package com.abba.tictacmed.infrastructure.messaging.whatsapp;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tictacmed.whatsapp")
public class WhatsAppProperties {
    /**
     * Enable WhatsApp reminders. Defaults to false to avoid accidental sends.
     */
    private boolean enabled = false;
    /**
     * Sender phone number in E.164 if using a provider; informational for now.
     */
    private String fromNumber;
    /**
     * Message template, supports placeholders: {patientName}, {medicineName}, {scheduledAt}.
     */
    private String template = "Hello {patientName}, it's time to take your {medicineName} at {scheduledAt}.";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getFromNumber() {
        return fromNumber;
    }

    public void setFromNumber(String fromNumber) {
        this.fromNumber = fromNumber;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}