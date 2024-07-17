package com.javaguru.identityservice.entity;

import com.javaguru.identityservice.service.Role;
import jakarta.persistence.*;
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
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]{1,12}$",
            message = "Format nickname: 'Johny' or 'JohnyAt2' or 'Johny-25', max 12 symbols")
    @Column(unique = true)
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
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();
}
