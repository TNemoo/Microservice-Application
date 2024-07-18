package com.javaguru.identityservice.service;

import com.javaguru.identityservice.dto.AuthReqPersonDto;
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


    // секретное слово для токена, находится в yml
    @Value("${spring.security.jwt.secret}")
    private String secret;

    // время жизни токена, находится в yml (3600000 = 1 час, 36000000 = 10 часов)
    @Value("${spring.security.jwt.expired}0")
    private long validityInMilliseconds;




    /** Регистрация нового пользователя */
    public ResponseEntity<PersonDto> save(PersonDto personDto) {
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
    private ResponseEntity<PersonDto> getPersonDtoResponseEntity(AuthReqPersonDto requestDto) {

        Person person = findByNickname(requestDto.getNickname());

        PersonDto personDto = modelMapper.map(person, PersonDto.class);

        final String token = generateToken(personDto);

        /** Создаем headers для ResponseEntity */
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        /** Создаем response, используя билдер класса ResponseEntity,
         * добавляя в него ранее созданный headers с токеном внутри и возвращаем его: */

        return ResponseEntity.ok()
                .headers(headers)
                .body(personDto);
    }


    public Person findByNickname(String nickname) {
        return personRepository.findByNickname(nickname)
                .orElseThrow(() -> new NotFoundException(String.format("User with nickname: %s not found", nickname)));
    }


    private Key getSigningKey() {
        byte[] keyByte = Decoders.BASE64.decode(secret);
        // декодирует строку secret из BASE64 в массив байт
        return Keys.hmacShaKeyFor(keyByte);
        // создает секретный ключ типа Key из массива байт keyByte с использованием алгоритма HMAC-SHA.
    }


    // Применяется в SS_JwtTokenFilter
    public boolean validateToken(String token) {
        try {
            // Парсинг и проверка JWT токена с использованием секретного ключа
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            // Проверка, не истек ли срок действия токена
            if (claims.getBody().getExpiration().before(new Date())) {
                log.info("IN validateToken - JWT token has expired");
                return false;  // Токен истек
            }

            log.info("IN validateToken - JWT token valid");
            return true;  // Токен действителен

        } catch (JwtException | IllegalArgumentException e) {
            log.info("IN validateToken - expired or invalid JWT token");
            return false;  // Токен недействителен или некорректен
        }
    }


    // Метод возвращает информацию о пользователе, его шифрованный пароль, активен ли пользователь и его роли
    public Authentication getAuthentication(String token) {
        // Используя секретное слово вычисляем nickname юзера: */
        String nickname = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        // Внимательно! Здесь parseClaimsJws, а не parseClaimsJwt. Метод parseClaimsJwt предназначен для анализа
        // несекретных токенов, а parseClaimsJws - для анализа JWT токенов с подписью

        // Находим в БД данные юзера
        Optional<Person> personOpt = personRepository.findByNickname(nickname);
        Person person = personOpt.orElseThrow(() -> {
            throw new NotFoundException(String.format("The nickname '%s' is not exist", nickname));
        });

        // Трансфер в стандартный SS entity класс UserDetails
        UserDetails userDetails = modelMapper.map(person, UserDetails.class);

        // Возвращаем стандартный для SS объект, собранный из UserDetails. Объект UsernamePasswordAuthenticationToken
        // является реализацией интерфейса Authentication в Spring Security. Этот объект представляет собой
        // токен аутентификации, который содержит информацию о пользователе и его правах доступа. */
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        // - principal (первый аргумент): Это объект, представляющий идентифицированного пользователя.
        // В большинстве случаев это объект UserDetails, который содержит информацию о пользователе, такую как
        // имя пользователя, пароль, активен ли пользователь и его роли.
        //  - credentials (второй аргумент): Это объект, представляющий учетные данные пользователя.
        // Обычно это пароль пользователя. Однако, после успешной аутентификации пароль может быть опущен (например,
        // установлен в пустую строку ""), так как он больше не нужен.
        // - authorities (третий аргумент): Это коллекция, представляющая роли или права доступа пользователя.
        // Она также может быть (!) получена из объекта UserDetails, а может передаваться из другого места. */
    }



    public String generateToken(PersonDto personDto) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, personDto);
    }



    public String createToken(Map<String, Object> claims, PersonDto personDto) {
        // Создаем список из String из списка enum Roles и помещаем его в тело:
        List<String> rolesS = personDto.getRoles().stream().map(Enum::name).toList();

        claims.put("roles", rolesS);

        String jws = Jwts.builder()
                .setClaims(claims) // claims - это Map<String, Object>, куда можно добавить любые данные
                //.claim("email", email) // можно например добавить почтовый адрес как дополнительное поле
                .setSubject(personDto.getNickname()) // устанавливаем никнейм как subject (sub) токена
                .setIssuedAt(new Date()) // Время выпуска токена
                .setExpiration(new Date(System.currentTimeMillis() + validityInMilliseconds)) // Время истечения токена (10 час)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Подпись токена
                .compact();

        return jws;
    }


    // Возвращает вырезанный из строки в хэдере токен
    public String cutTokenOut(HttpServletRequest req) {
        String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
        return bearerToken != null && bearerToken.startsWith("Bearer") ? bearerToken.substring(6) : null;
    }



    // Создает сущность JwtPerson на основе сущности Person
    public static JwtPersonDetails create(Person person) {
        List<GrantedAuthority> grantedAuthorities = person.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name())).collect(Collectors.toList());
        // SS требует, что бы список ролей был преобразован в контейнер из наследников GrantedAuthority

        return new JwtPersonDetails(person.getNickname(), person.getPassword(), grantedAuthorities);
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
