package com.javaguru.gateway.filter;

import com.javaguru.gateway.util.JwtUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouteValidator routeValidator;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouteValidator routeValidator, RestTemplate restTemplate, JwtUtil jwtUtil) {
        super(Config.class);
        this.routeValidator = routeValidator;
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
    }


    @Override
    public GatewayFilter apply(Config config) {
        /** такой синтаксис доступен, т.к. возвращаемый объект определен функциональным интерфейсом GatewayFilter
         * exchange - https-запрос, поступивший в gateway МСП */
        return (exchange, chain) -> {

            if(routeValidator.isSecured.test(exchange.getRequest())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("\nMissing authorization header\n");
                }

                String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

                //вырезаем токен, если он там есть
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    authHeader = authHeader.substring(7);
                } else {
                    throw new RuntimeException("\nInvalid authorization header format\n");
                }

                try {
                    //варианты откуда и как получать токены:
                    //restTemplate.getForObject("http://localhost:8080/auth/validatr?token=" + authHeader, String.class);
                    //String url = "http://IDENTITY-SERVICE/auth/validatr?token=" + authHeader;
                    //restTemplate.getForObject("http://localhost:8084/auth/validate?token=xxxxxxxxxxxxxxxxxxxxx");
                    //у нас пока упрощенка - созаем токен в классе Token
                    jwtUtil.validateToken(authHeader);
                } catch (Exception e) {
                    System.out.println("\ninvalid access...!\n");
                    throw new RuntimeException("un authorized access to application\n");
                }
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
        // Put the configuration properties here
    }
}
