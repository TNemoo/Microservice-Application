package com.javaguru.identityservice;

import com.javaguru.identityservice.security.AuthenticationPersonDto;
import com.javaguru.identityservice.service.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.*;

/** Класс, выводящий токен в консоль в учебных целях. В приложении не участвует */
public class Testing {
    public static void main(String[] args) {
        Map<String, Object> claims = new HashMap<>();
        AuthenticationPersonDto authPersonDto = new AuthenticationPersonDto("John", Collections.singleton(Role.ROLE_USER));
        createToken(claims, authPersonDto);
    }

    public static String createToken(Map<String, Object> claims, AuthenticationPersonDto authPersonDto) {
        // Создаем список из String из списка enum Roles и помещаем его в тело:
        List<String> rolesS = authPersonDto.roles().stream().map(v -> v.name()).toList();
        claims.put("roles", rolesS);

        String jws = Jwts.builder()
                .setClaims(claims) // claims - это Map<String, Object>, куда можно добавить любые данные
                //.claim("email", email) // можно например добавить почтовый адрес как дополнительное поле
                .setSubject(authPersonDto.nickname()) // устанавливаем никнейм как subject (sub) токена
                .setIssuedAt(new Date()) // Время выпуска токена
                .setExpiration(new Date(System.currentTimeMillis() + 36000000)) // Время истечения токена (10 час)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Подпись токена
                .compact();

        System.out.println(jws);

        return jws;
    }

    private static Key getSigningKey() {
        byte[] keyByte = Decoders.BASE64.decode("5367566859703373367639792F423F452848284D6251655468576D5A71347437");
        // декодирует строку secret из BASE64 в массив байт
        return Keys.hmacShaKeyFor(keyByte);
        // создает секретный ключ типа Key из массива байт keyByte с использованием алгоритма HMAC-SHA.
    }
}
