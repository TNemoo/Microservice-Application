//package com.javaguru.identityservice.jwt;
//
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.DefaultSecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
///* Этот класс добавляет фильтр в цепочку фильтров. Он делает совершенно тоже самое, что и строка в filterChain
//http.addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
//
//Так что этот класс избыточен в данной конфигурации SS.
//
//Нужен ли @Component или бин этого класса?. Класс JwtTokenFilterConfigurer можно использовать без аннотаций
//или явного объявления бина, поскольку он будет зарегистрирован в конфигурации безопасности через наследование
//от SecurityConfigurerAdapter. Когда вы создаете экземпляр SecurityConfigurerAdapter в конфигурации безопасности,
//он автоматически добавляется в цепочку фильтров.*/
//@RequiredArgsConstructor
//public class JwtTokenFilterConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    public void configure(HttpSecurity httpSecurity) {
//        JwtTokenFilter SSJwtTokenFilter = new JwtTokenFilter(jwtTokenProvider);
//        httpSecurity.addFilterBefore(SSJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
//    }
//}