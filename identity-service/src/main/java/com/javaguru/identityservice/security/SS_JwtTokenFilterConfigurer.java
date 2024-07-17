package com.javaguru.identityservice.security;


import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class SS_JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtService jwtService;

    @Override
    public void configure(HttpSecurity httpSecurity) {
        SS_JwtTokenFilter SSJwtTokenFilter = new SS_JwtTokenFilter(jwtService);
        httpSecurity.addFilterBefore(SSJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}