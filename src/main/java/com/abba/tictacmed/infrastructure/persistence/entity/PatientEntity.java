package com.abba.tictacmed.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Data
@Table(name = "patient")
public class PatientEntity {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "contact", nullable = false, length = 255, unique = true)
    private String contact;

    protected PatientEntity() {
    }

    public PatientEntity(UUID id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
    }

}
