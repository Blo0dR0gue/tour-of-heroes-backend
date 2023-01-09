package com.example.demo.auth.data;

import lombok.Value;

@Value
public class SignUpResponse {
    private boolean using2FA;
    private String qrCodeImage;
}