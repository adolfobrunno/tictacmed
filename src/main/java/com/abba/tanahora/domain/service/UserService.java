package com.abba.tanahora.domain.service;

import com.abba.tanahora.domain.model.User;

public interface UserService {

    User register(String whatsappId, String nome);
    void upgradePro(String whatsappId);

    User findByWhatsappId(String whatsappId);

}
