package com.mariayasmim.estetica.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestDTO(@NotBlank(message = "A mensagem não pode estar vazia") String message) {}
