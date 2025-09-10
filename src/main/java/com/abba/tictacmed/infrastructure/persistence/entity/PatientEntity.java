package com.abba.tictacmed.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;

import java.util.UUID;

@Entity
@Table(name = "patient")
public class PatientEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(org.hibernate.type.SqlTypes.BINARY)
    private UUID id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "contact", nullable = false, length = 255)
    private String contact;

    protected PatientEntity() {
    }

    public PatientEntity(UUID id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
