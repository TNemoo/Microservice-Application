package com.javaguru.identityservice.jwt;

import com.javaguru.identityservice.entity.Person;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

/* Класс JwtPersonFactory предназначен для создания объекта JwtPersonDetails на основе объекта Person.
JwtPersonDetails содержит информацию о пользователе, которая будет использоваться в процессе аутентификации и
авторизации в Spring Security. Основная цель этого класса — преобразование сущности Person в объект, пригодный для
использования Spring Security. */
@NoArgsConstructor
public final class JwtPersonFactory {

    // Создает сущность JwtPerson на основе сущности Person
    public static JwtPersonDetails create(Person person) {
        List<GrantedAuthority> grantedAuthorities = person.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
        // SS требует, что бы список ролей был преобразован в контейнер из наследников GrantedAuthority

        return new JwtPersonDetails(person.getNickname(), person.getPassword(), grantedAuthorities);
    }
}
