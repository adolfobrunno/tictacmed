package com.abba.tictacmed.infrastructure.persistence.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document(collection = "patient")
public class PatientEntity {

    private UUID id;

    private String name;

    @Id
    private String contact;

    protected PatientEntity() {
    }

    public PatientEntity(UUID id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

}
