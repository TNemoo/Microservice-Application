package com.javaguru.identityservice.controller;

import com.javaguru.identityservice.dto.AuthReqPersonDto;
import com.javaguru.identityservice.dto.AuthResPersonDto;
import com.javaguru.identityservice.dto.PersonDto;
import com.javaguru.identityservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    /** Регистрация нового пользователя. Безопасность обеспечивается автоматическим шифрованием тела запроса после
     * получения/создания и подключения в yml SSL сертификата */
    @PostMapping("/register")
    public ResponseEntity<?> registration (@RequestBody @Valid PersonDto personDto) {
        ResponseEntity<AuthResPersonDto> response = authService.save(personDto);
        return response;
    }


    /** Аутентификация - вход пользователя в систему по логин/пароль. Безопасность, см. выше */
    @PostMapping("/authentication")
    public ResponseEntity<?> authentication (@RequestBody @Valid AuthReqPersonDto requestDto) {
        ResponseEntity<?> response = authService.authentication(requestDto);
        return response;
    }


    /** Проверка токена в headers запроса, переданного юзером */
//    @GetMapping("/refresh-token")
//    public ResponseEntity<?> refreshToken (@RequestBody TokenRefreshRequest request) {
//        String newToken = authService.refreshToken(request.getRefreshToken(request));
//        return ResponseEntity.ok(new JwtResponse(newToken));
//        String token = authService.validateToken(response.getHeader("Authorization"));
//        return ResponseEntity.ok().body(null);
//    }
}

