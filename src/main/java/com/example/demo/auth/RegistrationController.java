package com.example.demo.auth;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.data.RegistrationRequestData;
import com.example.demo.user.UserService;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/v1/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequestData requestData) {
        return userService.registerUser(requestData);
    }
}