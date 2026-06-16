package ru.yandex.practicum.filmorate.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String error,
        String message,
        List<String> errors
) {
    public static ErrorResponse of(String error, String message) {
        return new ErrorResponse(error, message, null);
    }

    public static ErrorResponse of(String error, List<String> errors) {
        return new ErrorResponse(error, null, errors);
    }
}