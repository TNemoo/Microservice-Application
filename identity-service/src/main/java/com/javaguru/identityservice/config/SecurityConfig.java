package com.javaguru.identityservice.config;

import com.javaguru.identityservice.jwt.JwtTokenFilter;
import com.javaguru.identityservice.jwt.JwtTokenProvider;
import com.javaguru.identityservice.jwt.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtTokenFilter jwtTokenFilter;
    private final JwtTokenProvider jwtTokenProvider;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                    // разрешает доступ к любому запросу типа OPTIONS без аутентификации:
                    .requestMatchers(HttpMethod.OPTIONS).permitAll()
                    // эти URL доступны для всех:
                    .requestMatchers("/auth/register", "/auth/authentication").permitAll()
                    //только аутентифицированные пользователи с ролью ADMIN смогут получить доступ к этому URL:
                    .requestMatchers("/admin").hasRole("ADMIN")
                    // все URL, начинающиеся с /contacts/. Для этих URL требуется, чтобы пользователи имели хотя
                    // бы одну из ролей USER или ADMIN:
                    .requestMatchers("/contacts/**").hasAnyRole("USER", "ADMIN")
                    // прочие запросы - доступ для аутентифицированных пользователей вне зависимости от роли:
                    .anyRequest().authenticated()
            // если юзер не имеющий аутентификации прорывается по защищенному адресу, exceptionHandling
            // отправит ему response, создаваемый методом класса SS_RestAuthenticationEntryPoint:
            ).exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint));

        // Добавляет кастомный фильтр для обработки JWT-токенов в цепочку фильтров Spring Security перед стандартным
        // фильтром аутентификации на основе имени пользователя и пароля. Аргументы метода: первый является кастомным
        // фильтром, который добавляется в цепочку фильтров перед стандартным фильтром, указанным вторым аргументом.
        // UsernamePasswordAuthenticationFilter.class - это стандартный фильтр Spring Security, который обрабатывает
        // аутентификацию на основе имени пользователя и пароля
        http.addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}