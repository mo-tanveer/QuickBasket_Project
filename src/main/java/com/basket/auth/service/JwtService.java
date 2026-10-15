package com.basket.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey =
            Keys.hmacShaKeyFor(
                    "my-super-secret-key-for-quickbasket-1234567"
                            .getBytes()
            );
    private final long accessTokenExpiration = 15 * 60 * 1000;


    public String generateAccessToken(String email, String role)
    {
        return  Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("tokenType" , "Access")
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + accessTokenExpiration)
                )
                .signWith(secretKey)
                .compact();
    }


    public String extractEmail(String token)
    {

        return getClaims(token).getSubject();
    }

    public boolean isAccessTokenValid(String token)
    {
        try{
            Claims claims =  getClaims(token);
            return "ACCESS".equals(
                    claims.get("tokenType",String.class)
            );
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public Claims getClaims(String token){
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }
}
