package com.mariayasmim.estetica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequestDTO(
        @NotBlank
        @Email(message = "Email inválido")
        String email,

        @NotBlank
        String password
) {
}
