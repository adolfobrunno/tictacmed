package com.abba.tanahora.domain.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class PatientRef {

    private String id = UUID.randomUUID().toString();
    private String name;
    private OffsetDateTime createdAt = OffsetDateTime.now();
}
