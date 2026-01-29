package com.abba.tanahora.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collection = "medications")
@Data
public class Medication {

    @Id
    private UUID id = UUID.randomUUID();
    private String name;
    private String dosage;

    @DBRef private User user;

}
