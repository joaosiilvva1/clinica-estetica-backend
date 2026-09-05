package com.mariayasmim.estetica.dto;

import com.mariayasmim.estetica.entity.Photo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PhotoResponseDTO {
    private UUID id;
    private String url;
    private String title;
    private Integer sortOrder;
    private boolean active;

    public static PhotoResponseDTO from(Photo p) {
        return new PhotoResponseDTO(
                p.getId(), p.getUrl(), p.getTitle(), p.getSortOrder(), p.isActive()
        );
    }
}