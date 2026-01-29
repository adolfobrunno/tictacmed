package com.abba.tanahora.infrastructure.whatsapp;

import com.abba.tanahora.domain.model.MessageReceived;
import com.abba.tanahora.domain.service.MessageReceivedService;
import com.abba.tanahora.infrastructure.config.WhatsAppProperties;
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

    private static final Logger log = LoggerFactory.getLogger(WhatsAppWebhookController.class);

    private final WhatsAppProperties props;
    private final MessageReceivedService messageReceivedService;
    private final ObjectMapper objectMapper;

    public WhatsAppWebhookController(WhatsAppProperties props,
                                     MessageReceivedService messageReceivedService,
                                     ObjectMapper objectMapper) {
        this.props = props;
        this.messageReceivedService = messageReceivedService;
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
            // Typical structure from Meta: entry[].changes[].value.messages[], value.contacts[]
            if (root.has("entry") && root.get("entry").isArray()) {
                for (JsonNode entry : root.get("entry")) {
                    JsonNode changes = entry.path("changes");
                    if (!changes.isArray()) continue;
                    for (JsonNode change : changes) {
                        JsonNode value = change.path("value");

                        // Extrai nome do contato do primeiro elemento de contacts[]
                        String contactName = null;
                        JsonNode contacts = value.path("contacts");
                        if (contacts.isArray() && !contacts.isEmpty()) {
                            contactName = contacts.get(0).path("profile").path("name").asText(null);
                        }

                        JsonNode messages = value.path("messages");
                        if (!messages.isArray()) continue;
                        for (JsonNode msg : messages) {
                            String messageId = text(msg, "id");
                            String from = text(msg, "from");
                            String replyToMessageId = msg.path("context").path("id").asText(null);

                            // Support both text and interactive (button/list) messages
                            String bodyText;
                            String type = msg.path("type").asText(null);
                            if ("interactive".equals(type)) {
                                JsonNode interactive = msg.path("interactive");
                                // Prefer human-readable title; fallback to id
                                String btnTitle = interactive.path("button_reply").path("title").asText(null);
                                String btnId = interactive.path("button_reply").path("id").asText(null);
                                String listTitle = interactive.path("list_reply").path("title").asText(null);
                                String listId = interactive.path("list_reply").path("id").asText(null);
                                bodyText = firstNonBlank(btnId, btnTitle, listTitle, listId);
                            } else {
                                bodyText = msg.path("text").path("body").asText(null);
                            }

                            // Adiciona ao log o nome do contato, se disponível
                            if (messageId == null || from == null || bodyText == null || bodyText.isBlank()) {
                                log.debug("Skipping message due to missing fields: id={} from={} textPresent={} contactName={} type={}", messageId, mask(from), bodyText != null && !bodyText.isBlank(), contactName, type);
                                continue;
                            }

                            MessageReceived messageReceived = new MessageReceived();
                            messageReceived.setWhatsappId(from);
                            messageReceived.setMessage(bodyText);
                            messageReceived.setRepliedTo(replyToMessageId);
                            messageReceived.setContactName(contactName);
                            messageReceived.setId(messageId);
                            messageReceivedService.receiveMessage(messageReceived);

                            log.info("Persisted WhatsApp message id={} from={} contactName={} type={} length={}", messageId, mask(from), contactName, type == null ? "text" : type, bodyText.length());
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

    private String firstNonBlank(String... vals) {
        if (vals == null) return null;
        for (String s : vals) {
            if (s != null && !s.isBlank()) return s;
        }
        return null;
    }
}
