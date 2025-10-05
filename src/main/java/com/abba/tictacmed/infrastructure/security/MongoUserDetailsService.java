package com.abba.tictacmed.infrastructure.security;

import com.abba.tictacmed.infrastructure.persistence.entity.UserEntity;
import com.abba.tictacmed.infrastructure.persistence.repository.UserJpaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MongoUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    public MongoUserDetailsService(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity entity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        List<GrantedAuthority> authorities = parseAuthorities(entity.getRoles());
        return User.withUsername(entity.getUsername())
                .password(entity.getPasswordHash())
                .authorities(authorities)
                .build();
    }

    private List<GrantedAuthority> parseAuthorities(String roles) {
        if (roles == null || roles.isBlank()) return List.of();
        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(r -> r.startsWith("ROLE_") ? r : ("ROLE_" + r))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
