package com.javaguru.identityservice.security;

import com.javaguru.identityservice.service.Role;

import java.io.Serializable;
import java.util.Set;

public record AuthenticationPersonDto(String nickname, Set<Role> roles) implements Serializable {
}