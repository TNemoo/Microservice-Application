package com.javaguru.identityservice.jwt;


import com.javaguru.identityservice.entity.Person;
import com.javaguru.identityservice.exception.AlreadyExistException;
import com.javaguru.identityservice.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    @Override
    public UserDetails loadUserByUsername(String nickname) {
        Optional<Person> personOpt = personRepository.findByNickname(nickname);
        Person person = personOpt.orElseThrow(() -> {
            throw new AlreadyExistException(String.format("Nickname %s is already exist", nickname));
        });

        // Создаем сущность JwtPerson на основе Person как требует SS
        JwtPersonDetails jwtPersonDetails = JwtPersonFactory.create(person);
        log.info("IN loadUserByUsername -  loaded username: {} successfully loaded", nickname);

        return jwtPersonDetails;
    }
}
