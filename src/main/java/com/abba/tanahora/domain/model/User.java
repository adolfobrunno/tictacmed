package com.abba.tanahora.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@CompoundIndex(name = "whatsapp_idx", def = "{'whatsappId': 1}", unique = true)
@Data
public class User {

    @Id
    private String id;
    @Indexed
    private String whatsappId;
    private String name;
    private Plan plan = Plan.FREE;
    private OffsetDateTime proUntil;
    private OffsetDateTime proSince;
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @DBRef(lazy = true)
    private List<Medication> medications = new ArrayList<>();
    private List<PatientRef> patients = new ArrayList<>();

    public void enablePremium() {
        this.plan = Plan.PREMIUM;
        this.proSince = OffsetDateTime.now();
        this.proUntil = OffsetDateTime.now().plusMonths(1);
    }

    public boolean isPremium() {
        return plan == Plan.PREMIUM && proUntil != null && proUntil.isAfter(OffsetDateTime.now());
    }

}
