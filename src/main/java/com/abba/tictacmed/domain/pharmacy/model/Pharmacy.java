package com.abba.tictacmed.domain.pharmacy.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class Pharmacy {
    private final String cnpj;
    private final String name;
    private final String passwordHash;

    private Pharmacy(String cnpj, String name, String passwordHash) {
        this.cnpj = Objects.requireNonNull(cnpj, "cnpj");
        this.name = Objects.requireNonNull(name, "name");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
    }

    public static Pharmacy create(String cnpj, String name, String passwordHash) {
        return new Pharmacy(cnpj, name, passwordHash);
    }
}
