package com.example.demo.auth.data;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestData {
    @NotBlank
	private String username;

	@NotBlank
	private String password;
}
