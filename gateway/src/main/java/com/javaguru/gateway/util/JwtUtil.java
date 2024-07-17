package com.javaguru.gateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {
    public static final String SECRET = "5367566859703373367639792F423F452848284D6251655468576D5A71347437";
    /** Длина строки должна соответствовать условиям класса Jwts, создающего токены */

    private Key getSigningKey() {
        byte[] keyByte = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyByte);
    }

    /** Проверка токена по условию соответствия его изготовления строке SECRET */
    public void validateToken(final String token) {
        Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
        /** Внимательно! Здесь parseClaimsJws, а не parseClaimsJwt.
         * parseClaimsJwt предназначен для анализа несекретных токенов,
         * а parseClaimsJws - для анализа JWT токенов с подписью */
    }
}
