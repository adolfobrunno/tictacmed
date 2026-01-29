package com.abba.tanahora.infrastructure.whatsapp;

import com.abba.tanahora.application.notification.WhatsAppGateway;
import com.abba.tanahora.domain.model.User;
import com.abba.tanahora.infrastructure.config.WhatsAppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class WhatsAppGatewayImpl implements WhatsAppGateway {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppGatewayImpl.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String GRAPH_API_BASE = "https://graph.facebook.com/v19.0";

    private final WhatsAppProperties properties;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient = new OkHttpClient();

    public WhatsAppGatewayImpl(WhatsAppProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    @Override
    public String sendMessage(User user, String message, WhatsAppMessageType type) {
        if (!properties.isEnabled()) {
            log.info("[WhatsApp disabled] Skipping sendMessage");
            return "";
        }
        if (user == null || user.getWhatsappId() == null || user.getWhatsappId().isBlank()) {
            log.warn("Missing WhatsApp recipient id");
            return "";
        }
        if (message == null || message.isBlank()) {
            log.warn("Empty WhatsApp message for recipient={}", mask(user.getWhatsappId()));
            return "";
        }
        if (properties.getFromNumber() == null || properties.getFromNumber().isBlank()) {
            log.warn("Missing WhatsApp fromNumber in properties");
            return "";
        }
        if (properties.getAccessToken() == null || properties.getAccessToken().isBlank()) {
            log.warn("Missing WhatsApp accessToken in properties");
            return "";
        }

        String payload = buildPayload(user.getWhatsappId(), message, type);
        if (payload == null || payload.isBlank()) {
            log.warn("Unable to build WhatsApp payload for type={}", type);
            return "";
        }

        Request request = new Request.Builder()
                .url(GRAPH_API_BASE + "/" + properties.getFromNumber().trim() + "/messages")
                .post(RequestBody.create(payload, JSON))
                .addHeader("Authorization", "Bearer " + properties.getAccessToken())
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";
            if (!response.isSuccessful()) {
                log.warn("WhatsApp send failed status={} body={}", response.code(), responseBody);
                return "";
            }
            String messageId = extractMessageId(responseBody);
            log.info("WhatsApp message sent to={} id={}", mask(user.getWhatsappId()), messageId);
            return messageId;
        } catch (IOException e) {
            log.error("Failed to send WhatsApp message: {}", e.getMessage(), e);
            return "";
        }
    }

    private String buildPayload(String to, String message, WhatsAppMessageType type) {
        return switch (type) {
            case BASIC -> buildTextPayload(to, message);
            case BUTTONS -> buildButtonsPayload(to, message);
        };
    }

    private String buildTextPayload(String to, String message) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", to);
        payload.put("type", "text");
        payload.put("text", Map.of("body", message));
        return writeJson(payload);
    }

    private String buildButtonsPayload(String to, String message) {
        // Placeholder for interactive messages (buttons/lists) using the Meta API.
        throw new UnsupportedOperationException("Interactive button messages are not implemented yet.");
    }

    private String writeJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("Failed to serialize WhatsApp payload: {}", e.getMessage(), e);
            return "";
        }
    }

    private String extractMessageId(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "";
        }
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode messages = root.path("messages");
            if (messages.isArray() && !messages.isEmpty()) {
                return messages.get(0).path("id").asText("");
            }
        } catch (Exception e) {
            log.warn("Failed to parse WhatsApp response id: {}", e.getMessage());
        }
        return "";
    }

    private String mask(String v) {
        if (v == null || v.isBlank()) return "";
        if (v.length() <= 6) return "***";
        return v.substring(0, 3) + "***" + v.substring(v.length() - 3);
    }
}
