package com.javaguru.identityservice.jwt;


import com.javaguru.identityservice.entity.Person;
import com.javaguru.identityservice.repository.PersonRepository;
import com.javaguru.identityservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/*
UserDetailsService — это интерфейс Spring Security, предназначенный для загрузки данных пользователя по его имени.
Он определяет один метод loadUserByUsername, который создает сущность jwtPersonDetails, наследника стандартного
для SS класса UserDetails, из сущности Person, полученной из БД только по уникальному идентификатору. Проверка пароля
осуществляется в методе класса Authentication, вызывающем этот метод.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtPersonDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;
    private final AuthService authService;

    @Override
    public UserDetails loadUserByUsername(String nickname) {
        Person person = authService.findByNickname(nickname);
        // Создаем сущность JwtPerson на основе Person как требует SS
        JwtPersonDetails jwtPersonDetails = authService.create(person);
        log.info("IN loadUserByUsername -  loaded username: {} successfully loaded", nickname);

        return jwtPersonDetails;
    }
}
