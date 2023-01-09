package com.example.demo.auth;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.auth.data.RegistrationRequestData;
import com.example.demo.auth.data.SignUpResponse;
import com.example.demo.user.User;
import com.example.demo.user.UserService;

import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;

@RestController
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private QrDataFactory qrDataFactory;
    @Autowired
    private QrGenerator qrGenerator;

    @PostMapping("/api/v1/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid RegistrationRequestData requestData) {
        User user = userService.registerUser(requestData);
        if(user.isUsing2FA()){
            QrData data = qrDataFactory.newBuilder().label(user.getEmail()).secret(user.getSecret()).issuer("Panomenal").build();
            // Generate the QR code image data as a base64 string
            try {
                String qrCodeImg = getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType());
                return ResponseEntity.ok().body(new SignUpResponse(true, qrCodeImg));
            } catch (QrGenerationException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok(new SignUpResponse(false, null));
    }
}