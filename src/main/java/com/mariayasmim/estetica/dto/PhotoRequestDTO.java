package com.mariayasmim.estetica.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoRequestDTO {

    @NotBlank(message = "URL da foto é obrigatória")
    private String url;

    private String title;

    private Integer sortOrder;
}