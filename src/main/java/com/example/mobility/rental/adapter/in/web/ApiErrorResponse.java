package com.example.mobility.rental.adapter.in.web;

public record ApiErrorResponse(String code, String message) {
    public ApiErrorResponse {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Error code is required");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Error message is required");
        }
    }
}
