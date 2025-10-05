package com.abba.tictacmed.infrastructure.web.ui;

import com.abba.tictacmed.infrastructure.persistence.entity.PharmacyEntity;
import com.abba.tictacmed.infrastructure.persistence.entity.UserEntity;
import com.abba.tictacmed.infrastructure.persistence.repository.PharmacyJpaRepository;
import com.abba.tictacmed.infrastructure.persistence.repository.UserJpaRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/pharmacies")
public class PharmacyWebController {

    record PharmacyForm(@NotBlank String name, @NotBlank String cnpj, @NotBlank String adminUsername,
                        @NotBlank String adminPassword) {
    }

    private final PharmacyJpaRepository pharmacyRepository;
    private final UserJpaRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public PharmacyWebController(PharmacyJpaRepository pharmacyRepository,
                                 UserJpaRepository userRepository,
                                 UserDetailsService userDetailsService,
                                 PasswordEncoder passwordEncoder) {
        this.pharmacyRepository = pharmacyRepository;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new PharmacyForm("", "", "", ""));
        return "pharmacy-register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute("form") PharmacyForm form, BindingResult binding, Model model,
                                 jakarta.servlet.http.HttpServletRequest request,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            return "pharmacy-register";
        }
        // Save pharmacy without password
        PharmacyEntity entity = new PharmacyEntity(form.cnpj(), form.name());
        pharmacyRepository.save(entity);

        // Create admin user tied to this pharmacy
        String encoded = passwordEncoder.encode(form.adminPassword());
        UserEntity admin = new UserEntity(form.adminUsername(), encoded, "ADMIN", form.cnpj());
        userRepository.save(admin);

        // Programmatic login of the newly created admin user
        var userDetails = userDetailsService.loadUserByUsername(form.adminUsername());
        var authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), userDetails.getAuthorities());
        var context = org.springframework.security.core.context.SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        org.springframework.security.core.context.SecurityContextHolder.setContext(context);

        // Persist security context into HTTP session
        var session = request.getSession(true);
        session.setAttribute(org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        redirectAttributes.addFlashAttribute("welcomeMsg", "Bem-vindo, " + form.name() + "! Sua farmácia foi cadastrada com sucesso.");
        return "redirect:/ui/dashboard";
    }
}
