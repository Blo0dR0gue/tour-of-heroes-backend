package com.example.demo.error;

public class Invalid2FACodeException extends Exception{
    public Invalid2FACodeException(String message) {
        super(message);
    }
}
