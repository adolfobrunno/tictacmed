package com.abba.tanahora.application.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.api.domain.messages.Button;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InteractiveWhatsAppMessage implements WhatsAppMessage {

    private static final String MESSAGING_PRODUCT = "whatsapp";
    private static final String TYPE = "interactive";
    private static final String INTERACTIVE_TYPE = "button";

    private final String to;
    private final String text;
    private final List<Button> buttons;

    private InteractiveWhatsAppMessage(Builder builder) {
        this.to = builder.to;
        this.text = builder.text;
        this.buttons = List.copyOf(builder.buttons);
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

        Map<String, Object> interactive = new LinkedHashMap<>();
        interactive.put("type", INTERACTIVE_TYPE);
        interactive.put("body", Map.of("text", text));
        interactive.put("action", Map.of("buttons", buttons));
        payload.put("interactive", interactive);

        return writeJson(payload);
    }

    @Override
    public WhatsAppMessageType getType() {
        return WhatsAppMessageType.INTERACTIVE;
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
        private String text;
        private final List<Button> buttons = new ArrayList<>();

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder button(Button button) {
            this.buttons.add(button);
            return this;
        }

        public InteractiveWhatsAppMessage build() {
            return new InteractiveWhatsAppMessage(this);
        }
    }
}
