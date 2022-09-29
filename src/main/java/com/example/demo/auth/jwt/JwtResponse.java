package com.example.demo.auth.jwt;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class JwtResponse {
    private String token;
    private String tokenType = "Bearer";
    private int id;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, int id, String username, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
