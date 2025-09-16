package com.abba.tictacmed.infrastructure.web;

import com.abba.tictacmed.application.messaging.command.RegisterMessageReceivedCommand;
import com.abba.tictacmed.application.messaging.usecases.RegisterMessageReceived;
import com.abba.tictacmed.infrastructure.messaging.whatsapp.WhatsAppProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhooks/whatsapp")
public class WhatsAppWebhookController {

    private static final Logger log = LoggerFactory.getLogger(SchedulingController.class);

    private final WhatsAppProperties props;
    private final RegisterMessageReceived registerMessageReceived;
    private final ObjectMapper objectMapper;

    public WhatsAppWebhookController(WhatsAppProperties props,
                                     RegisterMessageReceived registerMessageReceived,
                                     ObjectMapper objectMapper) {
        this.props = props;
        this.registerMessageReceived = registerMessageReceived;
        this.objectMapper = objectMapper;
    }

    // Verification callback used by Meta when setting up the webhook
    @GetMapping
    public ResponseEntity<String> verify(@RequestParam(name = "hub.mode", required = false) String mode,
                                         @RequestParam(name = "hub.verify_token", required = false) String verifyToken,
                                         @RequestParam(name = "hub.challenge", required = false) String challenge) {
        if (!props.isEnabled()) {
            log.info("[WhatsApp disabled] Received verification request mode={} token={}.", mode, mask(verifyToken));
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if ("subscribe".equals(mode) && props.getVerifyToken() != null && props.getVerifyToken().equals(verifyToken)) {
            log.info("WhatsApp webhook verified successfully.");
            return ResponseEntity.ok(challenge != null ? challenge : "");
        }
        log.warn("WhatsApp webhook verification failed: mode={} token={}.", mode, mask(verifyToken));
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    // Receive events from WhatsApp Cloud API
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> receive(@RequestHeader Map<String, String> headers,
                                        @RequestBody(required = false) String body) {
        if (!props.isEnabled()) {
            log.info("[WhatsApp disabled] Received webhook POST body length={}", body == null ? 0 : body.length());
            return ResponseEntity.ok().build();
        }
        if (body == null || body.isBlank()) {
            log.warn("Empty webhook body");
            return ResponseEntity.ok().build();
        }
        try {
            JsonNode root = objectMapper.readTree(body);
            // Typical structure from Meta: entry[].changes[].value.messages[]
            if (root.has("entry") && root.get("entry").isArray()) {
                for (JsonNode entry : root.get("entry")) {
                    JsonNode changes = entry.path("changes");
                    if (!changes.isArray()) continue;
                    for (JsonNode change : changes) {
                        JsonNode value = change.path("value");
                        JsonNode messages = value.path("messages");
                        if (!messages.isArray()) continue;
                        for (JsonNode msg : messages) {
                            String messageId = text(msg, "id");
                            String from = text(msg, "from");
                            String bodyText = msg.path("text").path("body").asText(null);
                            if (messageId == null || from == null || bodyText == null) {
                                log.debug("Skipping message due to missing fields: id={} from={} textPresent={}", messageId, mask(from), bodyText != null);
                                continue;
                            }
                            registerMessageReceived.execute(new RegisterMessageReceivedCommand(messageId, from, bodyText));
                            log.info("Persisted WhatsApp message id={} from={} length={}", messageId, mask(from), bodyText.length());
                        }
                    }
                }
            } else {
                log.debug("Unexpected webhook JSON structure, no 'entry' array found");
            }
        } catch (Exception e) {
            log.error("Failed to process WhatsApp webhook: {}", e.getMessage(), e);
        }
        return ResponseEntity.ok().build();
    }

    private String text(JsonNode node, String field) {
        JsonNode v = node.get(field);
        return v != null && !v.isNull() ? v.asText() : null;
    }

    private String mask(String v) {
        if (v == null || v.isBlank()) return "";
        if (v.length() <= 6) return "***";
        return v.substring(0, 3) + "***" + v.substring(v.length() - 3);
    }
}
