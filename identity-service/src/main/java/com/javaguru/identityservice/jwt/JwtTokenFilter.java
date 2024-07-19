package com.javaguru.identityservice.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

/* JwtTokenFilter реализует фильтр, который перехватывает HTTP-запросы и проверяет наличие JWT токена в заголовке.
Если токен присутствует и валиден, он извлекает из него информацию об аутентификации и устанавливает её в контексте
безопасности Spring Security. Это позволяет обеспечивать безопасность на основе токенов. */
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    /* JwtTokenProvider – компонент, который предоставляет методы для работы с JWT токенами, такие как извлечение
     токена из заголовка, валидация токена и получение информации об аутентификации из токена: */
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain)
            throws IOException, ServletException {

        // вырезает токен из строки хэдера:
        String token = jwtTokenProvider.cutTokenOut((HttpServletRequest) req);
        //Этот метод проверяет, действителен ли токен (например, не истек ли срок его действия, не поврежден ли он и т.д.):
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // Этот метод извлекает информацию об аутентификации из токена, обычно включает в себя идентификатор пользователя и его роли:
            Authentication auth = jwtTokenProvider.getAuthentication(token);

            if (auth != null) {
                // Устанавливается в текущий контекст безопасности. Это позволяет Spring Security использовать
                // информацию о текущем пользователе в течение обработки запроса:
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        // Вызывает следующий фильтр в цепочке. Если этого не сделать, запрос не будет обработан дальше:
        filterChain.doFilter(req, res);
    }
}
