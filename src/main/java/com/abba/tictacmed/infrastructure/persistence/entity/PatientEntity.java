package com.abba.tictacmed.infrastructure.persistence.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "patient")
public class PatientEntity {

    @Id
    private String contact;
    private String name;

    protected PatientEntity() {
    }

    public PatientEntity(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

}
