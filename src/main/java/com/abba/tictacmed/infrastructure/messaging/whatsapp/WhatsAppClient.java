package com.abba.tictacmed.infrastructure.messaging.whatsapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatsapp.api.domain.messages.Button;
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

    public void sendInteractive(String to, String body, List<Button> buttons) {
        Objects.requireNonNull(to, "to");
        Objects.requireNonNull(body, "body");
        Objects.requireNonNull(buttons, "buttons");

        if (!properties.isEnabled()) {
            log.info("[WhatsApp disabled] Would send INTERACTIVE to={} body={} buttons={}", safe(to), safe(body),
                    buttons.stream().map(b -> safe(extractButtonTitle(b))).toList());
            return;
        }

        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("to", to);
            payload.put("type", "interactive");

            Map<String, Object> interactive = new LinkedHashMap<>();
            interactive.put("type", "button");

            Map<String, Object> bodyMap = new LinkedHashMap<>();
            bodyMap.put("text", body);
            interactive.put("body", bodyMap);

            Map<String, Object> action = new LinkedHashMap<>();

            // Build buttons as reply buttons according to WhatsApp Cloud API
            // https://developers.facebook.com/docs/whatsapp/cloud-api/reference/messages#interactive-object
//            List<Map<String, Object>> btnList = new ArrayList<>();
//            for (int i = 0; i < buttons.size(); i++) {
//                Button b = buttons.get(i);
//                String title = extractButtonTitle(b);
//                if (title == null || title.isBlank()) {
//                    title = "Option " + (i + 1);
//                }
//                String id = extractButtonId(b);
//                if (id == null || id.isBlank()) {
//                    id = "opt_" + (i + 1);
//                }
//                Map<String, Object> btn = new LinkedHashMap<>();
//                btn.put("type", "reply");
//                Map<String, Object> reply = new LinkedHashMap<>();
//                reply.put("id", id);
//                reply.put("title", title);
//                btn.put("reply", reply);
//                btnList.add(btn);
//            }
            action.put("buttons", buttons);
            interactive.put("action", action);

            payload.put("interactive", interactive);

            String json = objectMapper.writeValueAsString(payload);

            String phoneNumberId = properties.getFromNumber();
            String url = "https://graph.facebook.com/v20.0/" + phoneNumberId + "/messages";

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + properties.getAccessToken())
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(json, JSON))
                    .build();

            log.info("Sending INTERACTIVE message to WhatsApp: {}", json);
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
            log.error("Error calling WhatsApp API (interactive)", e);
        }
    }

    private String extractButtonTitle(Button button) {
        String val = callStringGetter(button, "getTitle", "getText", "getBody");
        return val;
    }

    private String extractButtonId(Button button) {
        String val = callStringGetter(button, "getId", "getPayload", "getValue");
        return val;
    }

    private String callStringGetter(Object target, String... methodNames) {
        for (String m : methodNames) {
            try {
                java.lang.reflect.Method method = target.getClass().getMethod(m);
                Object result = method.invoke(target);
                if (result instanceof String s) {
                    return s;
                }
            } catch (Exception ignored) {
                // try next
            }
        }
        return null;
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

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
