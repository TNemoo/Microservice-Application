package com.javaguru.identityservice.service;

import com.javaguru.identityservice.dto.PersonDto;
import com.javaguru.identityservice.entity.Person;
import com.javaguru.identityservice.exception.AlreadyExistException;
import com.javaguru.identityservice.repository.AuthRepository;
import com.javaguru.identityservice.security.AuthenticationPersonDto;
import com.javaguru.identityservice.security.JwtService;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;

import java.util.*;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;



    /** Регистрация нового пользователя */
    public ResponseEntity<PersonDto> save(PersonDto personDto) {
        Long id = personDto.getId();

        if (id != 0)
            throw new BadRequestException(String.format("The id = %s, bat new user can't have id", id));

        Optional<Person> personOpt;
        personOpt = authRepository.findByNickname(personDto.getNickname());
        personOpt.ifPresent(v -> {
            throw new AlreadyExistException(String.format("Nickname %s is already exist", personDto.getNickname()));
        });

        personOpt = authRepository.findByEmail(personDto.getEmail());
        personOpt.ifPresent(v -> {
            throw new AlreadyExistException(String.format("Email %s is already exist", personDto.getEmail()));
        });

        Person person = modelMapper.map(personDto, Person.class);
        person.setPassword(passwordEncoder.encode(person.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        person.setRoles(roles);
        Person savedPerson = authRepository.save(person);

//        AuthenticationPersonDto authPersonDto = modelMapper.map(savedPerson, AuthenticationPersonDto.class);

        PersonDto savedPersonDto  = modelMapper.map(savedPerson, PersonDto.class);
        savedPersonDto.setPassword("");

        return getPersonDtoResponseEntity(savedPersonDto);
    }



    /** Вход пользователя в систему по логин/пароль */
    public ResponseEntity<?> authentication(PersonDto personDto) {
        /** В Spring Security AuthenticationManager используется для выполнения аутентификации, для чего он использует
         *  метод UsernamePasswordAuthenticationToken(): */
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(personDto.getNickname(), personDto.getPassword()));
        return getPersonDtoResponseEntity(personDto);
    }



    private ResponseEntity<PersonDto> getPersonDtoResponseEntity(PersonDto personDto) {
        /** Генерируем токен на основе nickname юзера и секретного слова в нашем приложении: */
        final String token = jwtService.generateToken(modelMapper.map(personDto, AuthenticationPersonDto.class));

        /** Создаем headers для ResponseEntity */
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        /** Создаем response, используя билдер класса ResponseEntity,
         * добавляя в него ранее созданный headers с токеном внутри и возвращаем его: */
        return ResponseEntity.ok()
                .headers(headers)
                .body(personDto);
    }

//
//    public ResponseEntity<?> refreshToken(String refreshToken) {
//        /** Получаем объект класса Authentication (SS), который содержит информацию о пользователе,
//         * его шифрованный пароль, активен ли пользователь и его роли */
//        jwtService.getAuthentication(refreshToken);
//
//    }

    public Person findByNickname(String nickname) {
        return authRepository.findByNickname(nickname)
                .orElseThrow(() -> new NotFoundException(String.format("User with nickname: %s not found", nickname)));
    }
}
