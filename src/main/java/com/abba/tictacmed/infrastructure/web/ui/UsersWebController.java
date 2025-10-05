package com.abba.tictacmed.infrastructure.web.ui;

import com.abba.tictacmed.infrastructure.persistence.entity.UserEntity;
import com.abba.tictacmed.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/ui/users")
public class UsersWebController {

    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersWebController(UserJpaRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    public String createUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             Model model) {
        // Find current user's pharmacy
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = (auth != null ? auth.getName() : null);
        if (currentUsername == null) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        var current = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalStateException("Usuário atual não encontrado"));

        String hash = passwordEncoder.encode(password);
        UserEntity newUser = new UserEntity(username, hash, "ATTENDANT", current.getPharmacyCnpj());
        userRepository.save(newUser);

        model.addAttribute("userMsg", "Usuário criado com sucesso: " + username);
        return "dashboard";
    }
}
