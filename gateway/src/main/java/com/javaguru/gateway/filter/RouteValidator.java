package com.javaguru.gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    //назначение путей не требующих токена:
    public static final List<String> openApiEndpoints = List.of("/auth/register", "/auth/token", "/eureka");
    //проверка пути на соответствие одному из списка:
    public Predicate<ServerHttpRequest> isSecured = request -> openApiEndpoints.stream().noneMatch(
            uri -> request.getURI().getPath().contains(uri));
    //если соответствия не найдено, то требуется проверка токена
}
