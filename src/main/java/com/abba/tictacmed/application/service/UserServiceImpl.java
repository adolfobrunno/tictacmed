package com.abba.tictacmed.application.service;

import com.abba.tictacmed.domain.model.Plan;
import com.abba.tictacmed.domain.model.User;
import com.abba.tictacmed.domain.repository.UserRepository;
import com.abba.tictacmed.domain.service.UserService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(String whatsappId, String nome) {
        return userRepository.findByWhatsappId(whatsappId)
                .map(existing -> updateNameIfProvided(existing, nome))
                .orElseGet(() -> createNewUser(whatsappId, nome));
    }

    @Override
    public boolean isPro(User user) {
        if (user == null || user.getPlan() != Plan.PREMIUM) {
            return false;
        }
        OffsetDateTime proUntil = user.getProUntil();
        return proUntil == null || proUntil.isAfter(OffsetDateTime.now());
    }

    @Override
    public void upgradePro(String whatsappId) {
        User user = userRepository.findByWhatsappId(whatsappId).orElseGet(() -> createNewUser(whatsappId, null));
        user.enablePremium();
        userRepository.save(user);
    }

    private User updateNameIfProvided(User user, String name) {
        if (name != null && !name.isBlank()) {
            user.setName(name);
            userRepository.save(user);
        }
        return user;
    }

    private User createNewUser(String whatsappId, String name) {
        User user = new User();
        user.setWhatsappId(whatsappId);
        user.setName(name);
        user.setPlan(Plan.FREE);
        return userRepository.save(user);
    }
}
