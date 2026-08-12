package com.mariayasmim.estetica.exception;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ErrorResponseDTO(
        Instant timestamp,
        int status,
        String error,
        String message,
        List<Map<String, String>> fieldErrors
) {
    public static ErrorResponseDTO of(int status, String error, String message) {
        return new ErrorResponseDTO(Instant.now(), status, error, message, null);
    }

    public static ErrorResponseDTO ofValidation(int status, String error, List<Map<String, String>> fieldErrors) {
        return new ErrorResponseDTO(Instant.now(), status, error, "Erro de validação", fieldErrors);
    }
}
