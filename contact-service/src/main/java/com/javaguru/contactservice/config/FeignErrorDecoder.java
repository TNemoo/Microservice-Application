package com.javaguru.contactservice.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/** Настраиваем встроенный в Feign Client обработчик ошибок */
@NoArgsConstructor
public class FeignErrorDecoder implements ErrorDecoder {

    private final static Logger LOGGER = LoggerFactory.getLogger(FeignErrorDecoder.class);
    private static final String ERROR_MESSAGE_TEMPLATE = "Contacts service is not available, error %d";

    @Override
    public Exception decode(String methodKey, Response response) {
        String errorMessage = String.format(ERROR_MESSAGE_TEMPLATE, response.status());

        if (methodKey.contains("getIdByCode") || methodKey.contains("getCodeById")) {
            switch (response.status()) {
                case 400:
                    LOGGER.error(errorMessage);
                    return new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
                case 404:
                    LOGGER.error(errorMessage);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
                case 500:
                    LOGGER.error(errorMessage);
                    return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
                case 503:
                    LOGGER.error(errorMessage);
                    return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, errorMessage);
                default:
                    LOGGER.error(errorMessage);
                    return new RuntimeException(errorMessage);
            }
        } else {
            LOGGER.error(errorMessage);
            return new RuntimeException(errorMessage);
        }
    }
}