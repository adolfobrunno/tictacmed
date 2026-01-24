package com.abba.tictacmed.domain.repository;

import java.util.Optional;

import com.abba.tictacmed.domain.model.User;

public interface UserRepository {

    Optional<User> findByWhatsappId(String whatsappId);
    void save(User user);

}
