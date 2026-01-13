package com.example.appointmentsystem.exception;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
    }
}
