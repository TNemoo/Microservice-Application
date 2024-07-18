package com.javaguru.identityservice.jwt;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

//TODO: OK

//метод commence интерфейса AuthenticationEntryPoint вызывается при попытке не аутентифицированного доступа к
// защищенному ресурсу
@Component
public final class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());// Устанавливаем статус ответа 401 Unauthorized
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);// Устанавливаем тип контента как JSON
        // Создаем тело ответа в формате JSON:
        String body = new ObjectMapper().writeValueAsString(Collections.singletonMap("error", "Unauthorized Error"));
        // Записываем JSON-строку в выходной поток HTTP-ответа:
        response.getOutputStream().println(body);
    }
    // метод вносит изменения в объект response, т.е. HTTP-ответ, который будет отправлен клиенту.
}


