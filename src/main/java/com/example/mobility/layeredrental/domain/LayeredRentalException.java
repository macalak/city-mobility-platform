package com.example.mobility.layeredrental.domain;

import org.springframework.http.HttpStatus;

public class LayeredRentalException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public LayeredRentalException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }
}