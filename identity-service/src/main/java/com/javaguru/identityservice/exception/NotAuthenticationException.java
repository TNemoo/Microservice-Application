package com.javaguru.identityservice.exception;

public class NotAuthenticationException extends RuntimeException {
    public NotAuthenticationException(String message) {
        super(message);
    }
}
