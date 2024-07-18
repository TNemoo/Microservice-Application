package com.javaguru.identityservice.config;

import com.javaguru.identityservice.jwt.JwtTokenFilter;
import com.javaguru.identityservice.jwt.RestAuthenticationEntryPoint;
import com.javaguru.identityservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtTokenFilter jwtTokenFilter;

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
                    .requestMatchers("/auth/register", "/authentication").permitAll()
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
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

//    // Бин кастомного фильтра для токенов
//    @Bean
//    public JwtTokenFilter jwtTokenFilter() {
//        return new JwtTokenFilter(this.authService);
//    }
//
//    // Кодировщик паролей. Декодировщик не нужен, пароли не декодируются, они сравниваются.
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
}


//    roleHierarchy
//            OAuth2AuthorizationRequestRedirectFilter;
//    OAuth2LoginAuthenticationFilter; //Используются для обработки OAuth2 аутентификации и авторизации, что особенно важно в микросервисной архитектуре, где часто используются централизованные сервисы аутентификации (например, Identity Provider).
//
//            BearerTokenAuthenticationFilter;  //Обрабатывает JWT или OAuth2 Bearer Tokens, которые часто используются для аутентификации запросов между микросервисами.
//
//            SecurityContextPersistenceFilter //Обеспечивает восстановление SecurityContext из хранилища, что важно для поддержания состояния безопасности между запросами.
//
//            HeaderWriterFilter; //Добавляет заголовки безопасности (например, Content-Security-Policy, X-Content-Type-Options), чтобы улучшить защиту HTTP-заголовков.
//
//            LogoutFilter; //Обрабатывает запросы выхода, что может быть важно для управления сессиями пользователей в веб-приложениях.
//
//            RequestCacheAwareFilter; //Обеспечивает интеграцию с RequestCache, чтобы повторно отправлять запросы после успешной аутентификации.
//
//            SecurityContextHolderAwareRequestFilter; //Обеспечивает доступ к SecurityContext через HTTP-запрос.
//
//            ExceptionTranslationFilter; //Обрабатывает исключения безопасности, возникающие во время фильтрации запросов, и перенаправляет на страницу ошибки или другую точку входа.
//
//            FilterSecurityInterceptor; //Выполняет проверку авторизации для запросов.

/*

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                .requestMatchers("/auth", "/authentication", "/error").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new SS_JwtTokenFilterConfigurer(jwtService))
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .httpBasic().disable();
        return http.build();
    }
*/