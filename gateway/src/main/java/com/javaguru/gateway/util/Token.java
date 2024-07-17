package com.javaguru.gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

/** Вспомогательный класс вне приложения, создающий токен на основе SECRET приложения и выводящий его в консоль
 * Такой токен будет считаться валидным, поскольку он создан на основе секретной строки, а кому именно он выдан
 * пока в приложении не рассматривается*/
public class Token {
    public static void main(String[] args) {

        String secret = JwtUtil.SECRET;
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        String jws = Jwts.builder()
                .setSubject("admin@gmail.com") // Subject (пользователь)
                .setIssuer("example.com") // Издатель токена
                .setIssuedAt(new Date()) // Время выпуска токена
                .setExpiration(new Date(System.currentTimeMillis() + 36000000)) // Время истечения токена (10 час)
                .signWith(key, SignatureAlgorithm.HS256) // Подпись токена
                .compact();

        System.out.println("Generated Token: " + jws);
    }
}
