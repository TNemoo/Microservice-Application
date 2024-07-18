package com.javaguru.identityservice.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/* Этот класс сущностей создается для:
- удовлетворения требований SS относительно поля authorities
- получения стандартных методов, относящихся к аккаунтам
- оптимизации размера передаваемой сущности
 */

@RequiredArgsConstructor
public class JwtPersonDetails implements UserDetails {
    private final String nickname;
    private final String password;
    // SS требует, что бы список ролей был преобразован в контейнер из наследников GrantedAuthority
    private final Collection<? extends GrantedAuthority> authorities;

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true; //TODO: DB
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;//TODO: DB
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;//TODO: DB
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return true;//TODO: DB
    }
}