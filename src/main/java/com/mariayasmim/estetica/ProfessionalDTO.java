package com.mariayasmim.estetica.dto;

import com.mariayasmim.estetica.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfessionalDTO {

    private UUID id;
    private String name;

    public static ProfessionalDTO fromEntity(User user) {
        return ProfessionalDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}