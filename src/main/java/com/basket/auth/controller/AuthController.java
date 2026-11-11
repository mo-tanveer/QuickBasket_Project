package com.basket.auth.controller;

import com.basket.auth.dto.LoginRequest;
import com.basket.auth.dto.LoginResponse;
import com.basket.auth.dto.LoginResult;
import com.basket.auth.dto.RegisterRequest;
import com.basket.auth.service.JwtService;
import com.basket.auth.service.TokenBlacklistService;
import com.basket.auth.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;

    //constructor
    public AuthController(UserService userService,JwtService jwtService, TokenBlacklistService tokenBlacklistService)
    {

        this.userService = userService;
        this.jwtService = jwtService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request)
    {

        userService.registerUser(request);
        return "User registerd successfully";
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        LoginResult result = userService.loginUser(request);

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", result.refreshToken())
                .httpOnly(true)
                .secure(false)  //Local HTTP development only -changes Https with true at production
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(result.response());
    }


    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            HttpServletRequest request) {

        String rawRefreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    rawRefreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (rawRefreshToken == null || rawRefreshToken.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Refresh token cookie is missing"
            );
        }

       LoginResult result =
                userService.refreshAccessToken(rawRefreshToken);

        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", result.refreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(result.response());

    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){

        String rawRefreshToken = null;

        Cookie[] cookies = request.getCookies();

        if(cookies != null)
        {
            for(Cookie cookie: cookies){
                if("refreshToken".equals(cookie.getName())){
                    rawRefreshToken = cookie.getValue();
                    break;
                }
            }
        }

        //revoke refresh token
        if(rawRefreshToken != null && !rawRefreshToken.isBlank()){
            userService.logout(rawRefreshToken);
        }

        //blacklist access token
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (jwtService.isAccessTokenValid(token)) {
                    tokenBlacklistService.blacklist(
                            jwtService.extractJti(token),
                            jwtService.extractExpiration(token).getTime()
                    );
                }
            } catch (Exception ignored) {
                // token already invalid/expired, kuch karne ki zaroorat nahi
            }
        }

        //Clear refresh token cookie
        ResponseCookie clearCookie = ResponseCookie
                .from("refreshToken","")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/api/auth")
                .maxAge(Duration.ZERO)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body("Logged out successfully");
    }
}
