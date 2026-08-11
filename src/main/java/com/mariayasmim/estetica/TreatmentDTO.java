package com.mariayasmim.estetica.dto;

import com.mariayasmim.estetica.entity.Treatment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentDTO {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer durationMinutes;
    private Boolean active;

    public Treatment toEntity() {
        Treatment treatment = new Treatment();
        treatment.setId(this.id);
        treatment.setName(this.name);
        treatment.setDescription(this.description);
        treatment.setPrice(this.price);
        treatment.setDurationMinutes(this.durationMinutes);
        if (this.active != null) {
            treatment.setActive(this.active);
        }
        return treatment;
    }

    public static TreatmentDTO fromEntity(Treatment treatment) {
        return TreatmentDTO.builder()
                .id(treatment.getId())
                .name(treatment.getName())
                .description(treatment.getDescription())
                .price(treatment.getPrice())
                .durationMinutes(treatment.getDurationMinutes())
                .active(treatment.isActive())
                .build();
    }
}