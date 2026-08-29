package com.mariayasmim.estetica.controller;

import com.mariayasmim.estetica.dto.TreatmentRequestDTO;
import com.mariayasmim.estetica.dto.TreatmentResponseDTO;
import com.mariayasmim.estetica.service.TreatmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;

    // --- Público: lista de tratamentos ativos, pra home do site ---
    @GetMapping("/api/treatments/public")
    public ResponseEntity<List<TreatmentResponseDTO>> listPublic() {
        return ResponseEntity.ok(treatmentService.listActive());
    }

    // --- Administrativo: protegido por ROLE_ADMIN em SecurityConfig ---
    @GetMapping("/api/admin/treatments")
    public ResponseEntity<List<TreatmentResponseDTO>> listAll() {
        return ResponseEntity.ok(treatmentService.listAll());
    }

    @PostMapping("/api/admin/treatments")
    public ResponseEntity<TreatmentResponseDTO> create(@Valid @RequestBody TreatmentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(treatmentService.create(dto));
    }

    @PutMapping("/api/admin/treatments/{id}")
    public ResponseEntity<TreatmentResponseDTO> update(
            @PathVariable UUID id, @Valid @RequestBody TreatmentRequestDTO dto) {
        return ResponseEntity.ok(treatmentService.update(id, dto));
    }

    @PatchMapping("/api/admin/treatments/{id}/status")
    public ResponseEntity<TreatmentResponseDTO> setActive(
            @PathVariable UUID id, @RequestParam boolean active) {
        return ResponseEntity.ok(treatmentService.setActive(id, active));
    }
}
