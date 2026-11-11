package com.basket.auth.service;

import com.basket.auth.dto.LoginResponse;
import com.basket.auth.dto.LoginResult;
import com.basket.auth.dto.RegisterRequest;
import com.basket.auth.entity.Role;
import com.basket.auth.entity.User;
import com.basket.auth.refresh.RefreshToken;
import com.basket.auth.refresh.RefreshTokenService;
import com.basket.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.basket.auth.dto.LoginRequest;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public User registerUser( RegisterRequest request) {

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(Role.USER);
        user.setActive(true);

        if(userRepository.existsByEmail(request.getEmail()))
        {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email is already registered"
            );
        }
        return userRepository.save(user);
    }

    public LoginResult loginUser(LoginRequest request)
    {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new RuntimeException("Invalid  email or password"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException(("Invalid email or Password"));
        }

        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = refreshTokenService.createRefreshToken(user);

        LoginResponse response =new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                accessToken
        );

        return new LoginResult(response,refreshToken);
    }

    @Transactional
    public LoginResult refreshAccessToken(String rawRefreshToken) {

        RefreshToken oldToken = refreshTokenService
                .findValidRefreshToken(rawRefreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        User user = oldToken.getUser();

        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive");
        }

        // Revoke the old refresh token
        refreshTokenService.revokeRefreshToken(rawRefreshToken);

        // Generate a new access token
        String accessToken = jwtService.generateAccessToken(
                user.getEmail(),
                user.getRole().name()
        );

        // Generate a new refresh token
        String newRefreshToken = refreshTokenService.createRefreshToken(user);

        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                accessToken
        );

        return new LoginResult(response, newRefreshToken);
    }


    //logout
    public void logout(String rawRefreshToken)
    {
        refreshTokenService.revokeRefreshToken(rawRefreshToken);
    }
}