package com.mariayasmim.estetica.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.UUID;

public record AppointmentRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String clientName,

        @NotBlank(message = "WhatsApp é obrigatório")
        @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "WhatsApp inválido")
        String clientWhatsapp,

        @NotNull(message = "Profissional é obrigatório")
        UUID professionalId,

        @NotNull(message = "Tratamento é obrigatório")
        UUID treatmentId,

        @NotNull(message = "Horário é obrigatório")
        @Future(message = "Horário deve ser no futuro")
        Instant scheduledAt,

        String notes
) {
}
