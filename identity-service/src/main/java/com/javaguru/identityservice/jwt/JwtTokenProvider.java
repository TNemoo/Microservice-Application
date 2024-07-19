package com.javaguru.identityservice.jwt;

import com.javaguru.identityservice.dto.PersonDto;
import com.javaguru.identityservice.entity.Person;
import com.javaguru.identityservice.repository.PersonRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;

    // секретное слово для токена, находится в yml
    @Value("${spring.security.jwt.secret}")
    private String secret;

    // время жизни токена, находится в yml (3600000 = 1 час, 36000000 = 10 часов)
    @Value("${spring.security.jwt.expired}0")
    private long validityInMilliseconds;


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


    private Key getSigningKey() {
        byte[] keyByte = Decoders.BASE64.decode(secret);
        // декодирует строку secret из BASE64 в массив байт
        return Keys.hmacShaKeyFor(keyByte);
        // создает секретный ключ типа Key из массива байт keyByte с использованием алгоритма HMAC-SHA.
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



    // Возвращает вырезанный из строки в хэдере токен
    public String cutTokenOut(HttpServletRequest req) {
        String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
        return bearerToken != null && bearerToken.startsWith("Bearer") ? bearerToken.substring(6) : null;
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

}
