package com.abba.tictacmed.domain.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "medications")
@Data
public class Medication {

    @Id
    private UUID id = UUID.randomUUID();
    private String name;

    @DBRef private User user;

}
