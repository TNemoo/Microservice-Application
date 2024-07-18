package com.javaguru.identityservice.dto;

import com.javaguru.identityservice.service.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResPersonDto {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,12}$",
            message = "Format nickname: 'Johny' or 'JohnyAt2' or 'Johny-25', max 12 symbols")
    private String nickname;

    private Set<Role> roles = new HashSet<>();
}
