package com.mariayasmim.estetica.dto;

import com.mariayasmim.estetica.entity.Treatment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class TreatmentResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private boolean active;

    public static TreatmentResponseDTO from(Treatment t) {
        return new TreatmentResponseDTO(
                t.getId(), t.getName(), t.getDescription(),
                t.getPrice(), t.getDurationMinutes(), t.isActive()
        );
    }
}
