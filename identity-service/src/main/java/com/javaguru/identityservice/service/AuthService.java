package com.javaguru.identityservice.service;

import com.javaguru.identityservice.dto.AuthReqPersonDto;
import com.javaguru.identityservice.dto.AuthResPersonDto;
import com.javaguru.identityservice.dto.PersonDto;
import com.javaguru.identityservice.entity.Person;
import com.javaguru.identityservice.exception.AlreadyExistException;
import com.javaguru.identityservice.jwt.JwtPersonDetails;
import com.javaguru.identityservice.repository.PersonRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /** Дублирование с JwtTokenProvider */
    // секретное слово для токена, находится в yml
    @Value("${spring.security.jwt.secret}")
    private String secret;

    /** Дублирование с JwtTokenProvider */
    // время жизни токена, находится в yml (3600000 = 1 час, 36000000 = 10 часов)
    @Value("${spring.security.jwt.expired}0")
    private long validityInMilliseconds;


    // Регистрация нового пользователя
    public ResponseEntity<AuthResPersonDto> save(PersonDto personDto) {
        Long id = personDto.getId();

        if (id != 0)
            throw new BadRequestException(String.format("The id = %s, bat new user can't have id", id));

        Optional<Person> personOpt;
        personOpt = personRepository.findByNickname(personDto.getNickname());
        personOpt.ifPresent(v -> {
            throw new AlreadyExistException(String.format("Nickname %s is already exist", personDto.getNickname()));
        });

        personOpt = personRepository.findByEmail(personDto.getEmail());
        personOpt.ifPresent(v -> {
            throw new AlreadyExistException(String.format("Email %s is already exist", personDto.getEmail()));
        });

        Person person = modelMapper.map(personDto, Person.class);
        person.setPassword(passwordEncoder.encode(person.getPassword()));

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);

        person.setRoles(roles);
        Person savedPerson = personRepository.save(person);

        return getPersonDtoResponseEntity(modelMapper.map(savedPerson, AuthReqPersonDto.class));
        //возвращается nickname + roles
    }



    // Вход пользователя в систему по логин/пароль:
    public ResponseEntity<?> authentication(AuthReqPersonDto requestDto) {
        /* В Spring Security AuthenticationManager используется для выполнения аутентификации - сравнивает
        уникальный идентификатор юзера и пароль с БД, используя при этом метод authenticate().
        Когда вызывается authenticationManager.authenticate(...), Spring Security использует ProviderManager.
        ProviderManager вызывает метод authenticate на всех зарегистрированных AuthenticationProvider и его
        наследниках. В этом приложении DaoAuthenticationProvider и описано как сравнивать nickname/password.*/
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getNickname(), requestDto.getPassword()));
        //если авторизация провалится, метод authenticate выбросит exception;
        return getPersonDtoResponseEntity(requestDto); //возвращается nickname + roles
    }



    /* После проверки данных юзера, к nickname + roles в этом методе генерируем токен на основе nickname юзера
    и секретного слова в нашем приложении: */
    private ResponseEntity<AuthResPersonDto> getPersonDtoResponseEntity(AuthReqPersonDto authReqPersonDto) {

        Person person = findByNickname(authReqPersonDto.getNickname());

        AuthResPersonDto authResPersonDto = modelMapper.map(person, AuthResPersonDto.class);

        final String token = generateToken(authResPersonDto);

        /** Создаем headers для ResponseEntity */
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        /** Создаем response, используя билдер класса ResponseEntity,
         * добавляя в него ранее созданный headers с токеном внутри и возвращаем его: */

        return ResponseEntity.ok()
                .headers(headers)
                .body(authResPersonDto);
    }


    public Person findByNickname(String nickname) {
        return personRepository.findByNickname(nickname)
                .orElseThrow(() -> new NotFoundException(String.format("User with nickname: %s not found", nickname)));
    }


    public String generateToken(AuthResPersonDto authResPersonDto) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, authResPersonDto);
    }

    /** Дублирование с JwtTokenProvider */
    public String createToken(Map<String, Object> claims, AuthResPersonDto authResPersonDto) {
        // Создаем список из String из списка enum Roles и помещаем его в тело:
        List<String> rolesS = authResPersonDto.getRoles().stream().map(Enum::name).toList();

        claims.put("roles", rolesS);

        String jws = Jwts.builder()
                .setClaims(claims) // claims - это Map<String, Object>, куда можно добавить любые данные
                //.claim("email", email) // можно например добавить почтовый адрес как дополнительное поле
                .setSubject(authResPersonDto.getNickname()) // устанавливаем никнейм как subject (sub) токена
                .setIssuedAt(new Date()) // Время выпуска токена
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds)) // Время истечения токена (10 час)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Подпись токена
                .compact();

        return jws;
    }

    /** Дублирование с JwtTokenProvider */
    private Key getSigningKey() {
        byte[] keyByte = Decoders.BASE64.decode(secret);
        // декодирует строку secret из BASE64 в массив байт
        return Keys.hmacShaKeyFor(keyByte);
        // создает секретный ключ типа Key из массива байт keyByte с использованием алгоритма HMAC-SHA.
    }
}



    /*// Этот метод аналогичен методу authentication, но не использует возможности SS
    public AuthResPersonDto findByEmailAndPassword(AuthReqPersonDto requestDto) {
        Optional<Person> personOpt = personRepository.
                findByNicknameAndPassword(requestDto.getNickname(), requestDto.getPassword());

        Person person = personOpt.orElseThrow(()
                -> { throw new NotAuthenticationException("The user's data is incorrect"); });

        return modelMapper.map(person, AuthResPersonDto.class);
    } */
