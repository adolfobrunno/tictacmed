package com.abba.tictacmed.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AiMessageProcessorDto {

    @JsonProperty(required = true)
    String rrule;
    @JsonProperty(required = true)
    String medication;
    @JsonProperty(required = true)
    MessageReceivedType type;
    @JsonProperty(required = true)
    String dosage;

}
