package com.example.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.auth.data.RegistrationRequestData;
import com.example.demo.error.UserAlreadyExistAuthenticationException;
import com.example.demo.role.ERole;
import com.example.demo.role.Role;
import com.example.demo.role.RoleRepository;

import dev.samstevens.totp.secret.SecretGenerator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private SecretGenerator secretGenerator;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public String generate2FASecret() {
        return secretGenerator.generate();
    }

    public void checkIfUserExists(String username, String email){
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistAuthenticationException(
                    "User with Username: " + username + " already exist");
        }

        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistAuthenticationException(
                    "User with E-Mail: " + email + " already exist");
        }
    }

    public User registerUser(RegistrationRequestData requestData) {
        this.checkIfUserExists(requestData.getUsername(), requestData.getEmail());

        // Create new user's account
        User user = new User(requestData.getUsername(),
                requestData.getEmail(),
                passwordEncoder.encode(requestData.getPassword()));

        user.setEnabled(true);

        if (requestData.isUsing2FA()) {
            user.setUsing2FA(true);
            user.setSecret(requestData.getSecret());
        }

        String strRole = requestData.getRole();
        Role role = null;

        if (strRole == null) {
            role = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } else {
            switch (strRole) {
                case "admin":
                    role = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                    break;
                default:
                    role = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(() -> new RuntimeException("Error: Role not found."));
            }
        }

        user.setRole(role);
        userRepository.save(user);
        return user;
    }

}