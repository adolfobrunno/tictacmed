package com.abba.tictacmed.infrastructure.messaging.whatsapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.api.domain.messages.TextMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Thin wrapper around Meta WhatsApp Cloud API using OkHttp.
 * For now we support:
 * - simple text messages
 * - "text with buttons" fallback (sends the text plus a formatted list of options)
 * <p>
 * Notes:
 * - When WhatsApp is disabled via properties, this client will only log the
 * message that would be sent and return without calling the external API.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsAppClient {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final WhatsAppProperties properties;
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient = new OkHttpClient();

    public void sendText(String to, String body) {
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(body, "body");

        if (!properties.isEnabled()) {
            log.info("[WhatsApp disabled] Would send TEXT to={} body={}", safe(to), safe(body));
            return;
        }

        // Use library class to compose the message content
        TextMessage textMessage = new TextMessage().setBody(body);
        sendViaMetaApi(to, textMessage);
        log.info("WhatsApp TEXT sent to={}", safe(to));
    }

    /**
     * Sends a message with a list of buttons.
     * Minimal implementation formats buttons as a numbered list and sends as plain text.
     * If you want true interactive buttons later, swap internals to Interactive message payload.
     */
    public void sendTextWithButtons(String to, String body, List<String> buttons) {
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(body, "body");
        Objects.requireNonNull(buttons, "buttons");

        String formatted = formatWithButtons(body, buttons);

        if (!properties.isEnabled()) {
            log.info("[WhatsApp disabled] Would send BUTTONS to={} body={} buttons={}", safe(to), safe(body), buttons);
            return;
        }

        TextMessage textMessage = new TextMessage().setBody(formatted);
        sendViaMetaApi(to, textMessage);
        log.debug("WhatsApp BUTTONS (as text) sent to={}", safe(to));
    }

    private void sendViaMetaApi(String to, TextMessage textMessage) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("to", to);
            payload.put("type", "text");
            Map<String, Object> text = new LinkedHashMap<>();
            text.put("body", textMessage.getBody());
            text.put("preview_url", Boolean.FALSE);
            payload.put("text", text);

            String json = objectMapper.writeValueAsString(payload);

            String phoneNumberId = properties.getFromNumber();
            String url = "https://graph.facebook.com/v20.0/" + phoneNumberId + "/messages";

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + properties.getAccessToken())
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(json, JSON))
                    .build();

            log.info("Sending message to WhatsApp: {}", json);
            log.info("URL: {}", request.url());
            log.info("Headers: {}", request.headers());

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    log.error("WhatsApp API call failed: code={} body={}", response.code(), errorBody);
                }
                log.info("WhatsApp API call response: code={} body={}", response.code(), response.body());
            }
        } catch (IOException e) {
            log.error("Error calling WhatsApp API", e);
        }
    }

    private String formatWithButtons(String body, List<String> buttons) {
        StringBuilder sb = new StringBuilder(body == null ? "" : body.trim());
        if (buttons != null && !buttons.isEmpty()) {
            sb.append("\n\n");
            int idx = 1;
            for (String b : buttons) {
                if (b == null || b.isBlank()) continue;
                sb.append(idx++).append(") ").append(b.trim()).append("\n");
            }
        }
        return sb.toString();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
