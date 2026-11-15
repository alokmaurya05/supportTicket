package com.example.supportticket.config;

import com.example.supportticket.entity.AppUser;
import com.example.supportticket.entity.Role;
import com.example.supportticket.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedDataConfig {
    @Bean CommandLineRunner seedUsers(UserRepository users, PasswordEncoder passwords) {
        return args -> {
            createIfMissing(users, passwords, "support", "support1234", Role.SUPPORT);
            createIfMissing(users, passwords, "admin", "admin1234", Role.ADMIN);
        };
    }
    private void createIfMissing(UserRepository users, PasswordEncoder passwords, String username, String password, Role role) {
        if (!users.existsByUsername(username)) users.save(new AppUser(username, passwords.encode(password), role));
    }
}
