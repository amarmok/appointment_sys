package com.example.appointmentsystem.exception;

import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public record ApiErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static ApiErrorResponse of(HttpStatus status, String message, String path) {
        return new ApiErrorResponse(
                OffsetDateTime.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
    }
}
