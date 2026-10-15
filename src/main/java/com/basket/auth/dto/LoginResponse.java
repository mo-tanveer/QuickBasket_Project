package com.basket.auth.dto;
import com.basket.auth.entity.Role;
import lombok.Data;

@Data
public class LoginResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String accessToken;
    public LoginResponse(Long id, String name, String email, Role role,String accessToken)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.accessToken = accessToken;
    }
}
