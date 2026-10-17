package com.basket.auth.dto;

public record LoginResult(
        LoginResponse response,
        String refreshToken
) {
}
