package com.abba.tictacmed.domain.service;

import com.abba.tictacmed.domain.model.User;

public interface UserService {

    User register(String whatsappId, String nome);
    boolean isPro(User usuario);
    void upgradePro(String whatsappId);

}
