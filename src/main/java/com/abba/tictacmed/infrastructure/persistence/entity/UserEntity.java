package com.abba.tictacmed.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class UserEntity {

    @Id
    @Indexed(unique = true)
    private String username;

    private String passwordHash;

    private String roles; // comma-separated, e.g., "ADMIN" or "ATTENDANT"

    private String pharmacyCnpj;
}
