package com.basket.auth.service;

import com.basket.auth.dto.LoginResponse;
import com.basket.auth.dto.RegisterRequest;
import com.basket.auth.entity.Role;
import com.basket.auth.entity.User;
import com.basket.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.basket.auth.dto.LoginRequest;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User registerUser( RegisterRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.USER);
        user.setActive(true);

        return userRepository.save(user);
    }

    public LoginResponse loginUser(LoginRequest request)
    {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException("Invalid  email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException(("Invalid email or Password"));
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );
        return new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                token
        );
    }
}