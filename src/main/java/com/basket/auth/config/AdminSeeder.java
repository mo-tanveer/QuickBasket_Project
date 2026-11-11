package com.basket.auth.config;

import com.basket.auth.entity.Role;
import com.basket.auth.entity.User;
import com.basket.auth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        String adminEmail = "admin@gmail.com";

        if (userRepository.existsByEmail(adminEmail)) {
            return;   // already bana hua hai
        }

        User admin = new User();
        admin.setName("Admin");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("Admin@12345"));
        admin.setRole(Role.ADMIN);
        admin.setActive(true);

        userRepository.save(admin);
        System.out.println("Default admin created: " + adminEmail);
    }
}