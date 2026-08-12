package com.mariayasmim.estetica.dto;

public record AuthResponseDTO(
        String token,
        String tokenType,
        long expiresInMs
) {
    public static AuthResponseDTO of(String token, long expiresInMs) {
        return new AuthResponseDTO(token, "Bearer", expiresInMs);
    }
}
