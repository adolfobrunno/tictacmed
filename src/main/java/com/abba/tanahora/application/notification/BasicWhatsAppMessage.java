package com.abba.tanahora.application.notification;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

public class BasicWhatsAppMessage implements WhatsAppMessage {

    private static final String MESSAGING_PRODUCT = "whatsapp";
    private static final String TYPE = "text";

    private final String to;
    private final String message;

    private BasicWhatsAppMessage(Builder builder) {
        this.to = builder.to;
        this.message = builder.message;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String buildPayload() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("messaging_product", MESSAGING_PRODUCT);
        payload.put("to", to);
        payload.put("type", TYPE);
        payload.put("text", Map.of("body", message));
        return writeJson(payload);
    }

    @Override
    public WhatsAppMessageType getType() {
        return WhatsAppMessageType.BASIC;
    }

    private String writeJson(Object payload) {
        try {
            return new ObjectMapper().writeValueAsString(payload);
        } catch (Exception e) {
            return "";
        }
    }

    public static final class Builder {
        private String to;
        private String message;

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public BasicWhatsAppMessage build() {
            return new BasicWhatsAppMessage(this);
        }
    }
}
