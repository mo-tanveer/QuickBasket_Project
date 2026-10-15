package com.basket.auth.controller;

import com.basket.auth.dto.LoginRequest;
import com.basket.auth.dto.LoginResponse;
import com.basket.auth.dto.RegisterRequest;
import com.basket.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private UserService userService;

    //constructor
    public AuthController(UserService userService)
    {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request)
    {

        userService.registerUser(request);
        return "User registerd successfully";
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {

        System.out.println(">>> LOGIN API HIT <<<");

        LoginResponse response = userService.loginUser(request);

        System.out.println(">>> LOGIN SUCCESS <<<");

        return response;
    }

}
