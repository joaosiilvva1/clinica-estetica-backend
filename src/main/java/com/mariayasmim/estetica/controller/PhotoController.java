package com.mariayasmim.estetica.controller;

import com.mariayasmim.estetica.dto.PhotoRequestDTO;
import com.mariayasmim.estetica.dto.PhotoResponseDTO;
import com.mariayasmim.estetica.service.PhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    // --- Público: fotos ativas, na ordem certa, pro carrossel da home ---
    @GetMapping("/api/photos/public")
    public ResponseEntity<List<PhotoResponseDTO>> listPublic() {
        return ResponseEntity.ok(photoService.listActive());
    }

    // --- Administrativo: protegido por ROLE_ADMIN em SecurityConfig ---
    @GetMapping("/api/admin/photos")
    public ResponseEntity<List<PhotoResponseDTO>> listAll() {
        return ResponseEntity.ok(photoService.listAll());
    }

    @PostMapping("/api/admin/photos")
    public ResponseEntity<PhotoResponseDTO> create(@Valid @RequestBody PhotoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(photoService.create(dto));
    }

    @PutMapping("/api/admin/photos/{id}")
    public ResponseEntity<PhotoResponseDTO> update(
            @PathVariable UUID id, @Valid @RequestBody PhotoRequestDTO dto) {
        return ResponseEntity.ok(photoService.update(id, dto));
    }

    @PatchMapping("/api/admin/photos/{id}/status")
    public ResponseEntity<PhotoResponseDTO> setActive(
            @PathVariable UUID id, @RequestParam boolean active) {
        return ResponseEntity.ok(photoService.setActive(id, active));
    }

    @DeleteMapping("/api/admin/photos/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        photoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}