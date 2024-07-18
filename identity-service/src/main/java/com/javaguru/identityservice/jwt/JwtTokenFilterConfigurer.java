package com.javaguru.identityservice.jwt;


import com.javaguru.identityservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final AuthService authService;

    //TODO: 1
    @Override
    public void configure(HttpSecurity httpSecurity) {
        JwtTokenFilter SSJwtTokenFilter = new JwtTokenFilter(authService);
        httpSecurity.addFilterBefore(SSJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}