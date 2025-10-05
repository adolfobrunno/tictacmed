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
    private String status; // PENDING_CONFIRMATION or ACTIVE
    private String confirmationCode; // nullable

    protected PatientEntity() {
    }

    public PatientEntity(String name, String contact, String status, String confirmationCode) {
        this.name = name;
        this.contact = contact;
        this.status = status;
        this.confirmationCode = confirmationCode;
    }

}
