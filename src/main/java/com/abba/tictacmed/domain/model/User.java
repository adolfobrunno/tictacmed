package com.abba.tictacmed.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Document(collection = "users")
@CompoundIndex(name = "whatsapp_idx", def = "{'whatsappId': 1}", unique = true)
@Data
public class User {

    @Id
    private String id;
    @Indexed
    private String whatsappId; // "5511999999999"
    private String name;
    private Plan plan = Plan.FREE;
    private LocalDateTime proUntil;

    @DBRef(lazy = true) // ref para performance
    private List<Medication> medications = new ArrayList<>();

}
