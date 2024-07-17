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
public class PersonDto {

    private Long id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,12}$",
            message = "Format nickname: 'Johny' or 'JohnyAt2' or 'Johny-25', max 12 symbols")
    private String nickname;

    @NotBlank
    @Pattern(regexp = "^[A-ZА-я][a-zа-я]{0,49}$",
            message = "Format name: 'John' or 'Джон', max 50 symbols")
    private String firstname;

    @NotBlank
    @Pattern(regexp = "^[A-ZА-я][a-zа-я]{0,49}$",
            message = "Format name: 'Doe' or 'Доу', max 50 symbols")
    private String surname;

    @NotBlank
    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}",
            message = "Format e-mail address: 'example@example.com'")
    private String email;

    @Pattern(regexp = "^[a-zA-Z0-9-]{1,12}$",
            message = "Format password: 'eXAmple22-eX'")
    private String password;

    private Set<Role> roles = new HashSet<>();
}
