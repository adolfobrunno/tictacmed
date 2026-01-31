package com.abba.tanahora.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

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
    @JsonProperty(required = true)
    LocalDate startDate;

}
