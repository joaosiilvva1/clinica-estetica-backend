package com.mariayasmim.estetica.dto;

import com.mariayasmim.estetica.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateDTO(
        @NotNull(message = "Status é obrigatório")
        AppointmentStatus status
) {
}
