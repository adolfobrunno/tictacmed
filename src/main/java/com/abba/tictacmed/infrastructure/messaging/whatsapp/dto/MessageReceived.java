package com.abba.tictacmed.infrastructure.messaging.whatsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record MessageReceived(@JsonProperty(required = true) MessageReceivedType type,
                              @JsonProperty(required = true) String medicine,
                              @JsonProperty(required = true) OffsetDateTime start,
                              @JsonProperty(required = true) OffsetDateTime end,
                              @JsonProperty(required = true) Long frequency,
                              @JsonProperty(required = true) Boolean recurring) {

    public MessageReceived(MessageReceivedType type, String medicine) {
        this(type, medicine, null, null, null, null);
    }
}
