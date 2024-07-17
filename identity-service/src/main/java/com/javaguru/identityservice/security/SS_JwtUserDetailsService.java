package com.javaguru.identityservice.security;


import com.javaguru.identityservice.entity.Person;
import com.javaguru.identityservice.repository.AuthRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SS_JwtUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String nickname) {
        Optional<Person> personOpt = authRepository.findByNickname(nickname);

        Person person = personOpt.orElseThrow(() -> {
            throw new NotFoundException(String.format("User with username: %s not found", nickname));
        });

        JwtPerson jwtPerson = jwtService.create(person);
        log.info("IN loadUserByUsername -  loaded username: {} successfully loaded", person);
        return jwtPerson;
    }
}
