package com.javaguru.identityservice.security;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public class AuthenticationPersonRequestDto implements Serializable {
        @NotBlank
        String email;

        @NotBlank
        String password;
}
