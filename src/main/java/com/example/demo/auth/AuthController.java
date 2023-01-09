package com.example.demo.auth;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.AppConstants;
import com.example.demo.auth.data.LoginRequestData;
import com.example.demo.auth.jwt.JwtResponse;
import com.example.demo.auth.jwt.JwtUtils;
import com.example.demo.auth.services.UserDetailsImpl;
import com.example.demo.auth.services.UserDetailsServiceImpl;
import com.example.demo.error.AuthenticationException;
import com.example.demo.error.Invalid2FACodeException;
import com.example.demo.role.RoleRepository;
import com.example.demo.user.UserRepository;

import dev.samstevens.totp.code.CodeVerifier;

@Controller
@RequestMapping(path = AppConstants.AUTH_URL)
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	CodeVerifier verifier;

	@PostMapping("/login")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestData loginRequest) {
		authenticate(loginRequest.getUsername(), loginRequest.getPassword());

		UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
		boolean authenticated = !userDetails.isUsing2FA();

		String jwt = jwtUtils.generateToken(userDetails, authenticated);

		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt,
				userDetails.getId(),
				userDetails.getUsername(),
				userDetails.getEmail(),
				roles,
				authenticated));
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
		String authToken = request.getHeader(AppConstants.AUTH_HEADER);
		final String token = authToken.substring(7);
		String username = jwtUtils.getUsernameFromToken(token);
		userDetailsService.loadUserByUsername(username);

		if (jwtUtils.canTokenBeRefreshed(token)) {
			String refreshedToken = jwtUtils.refreshToken(token);
			// Just send back the token
			return ResponseEntity.ok(new JwtResponse(refreshedToken));
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}

	//TODO: verify 2fa code bevor creating the account.
	@PostMapping("/verify")
	@PreAuthorize("hasRole('ROLE_PRE_VERIFICATION_USER')")
	public ResponseEntity<?> verifyCode(@NotEmpty @RequestBody String code,
			@AuthenticationPrincipal UserDetailsImpl userDetails) throws Invalid2FACodeException {
		if (!verifier.isValidCode(userDetails.getSecret(), code)) {
			throw new Invalid2FACodeException("Invalid Code");
		}
		String jwt = jwtUtils.generateToken(userDetails, true);

		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt,
				userDetails.getId(),
				userDetails.getUsername(),
				userDetails.getEmail(),
				roles,
				true));
	}

	private void authenticate(String username, String password) {
		Objects.requireNonNull(username);
		Objects.requireNonNull(password);

		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new AuthenticationException("User is disabled", e);
		} catch (BadCredentialsException e) {
			throw new AuthenticationException("Credentials are invalid", e);
		}
	}

}
