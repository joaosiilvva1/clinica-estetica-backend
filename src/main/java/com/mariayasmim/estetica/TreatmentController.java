package com.mariayasmim.estetica.controller;

import com.mariayasmim.estetica.dto.TreatmentDTO;
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

    // --- Público: catálogo que a cliente final vê antes de agendar ---

    @GetMapping("/api/treatments/public")
    public ResponseEntity<List<TreatmentDTO>> listPublicCatalog() {
        return ResponseEntity.ok(treatmentService.listActive());
    }

    @GetMapping("/api/treatments/public/{id}")
    public ResponseEntity<TreatmentDTO> getPublicTreatment(@PathVariable UUID id) {
        return ResponseEntity.ok(treatmentService.getById(id));
    }

    // --- Administrativo: gestão do catálogo, protegido por ROLE_ADMIN em SecurityConfig ---

    @GetMapping("/api/admin/treatments")
    public ResponseEntity<List<TreatmentDTO>> listAllForAdmin() {
        return ResponseEntity.ok(treatmentService.listAll());
    }

    @PostMapping("/api/admin/treatments")
    public ResponseEntity<TreatmentDTO> create(@Valid @RequestBody TreatmentDTO dto) {
        TreatmentDTO created = treatmentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/api/admin/treatments/{id}")
    public ResponseEntity<TreatmentDTO> update(@PathVariable UUID id, @Valid @RequestBody TreatmentDTO dto) {
        return ResponseEntity.ok(treatmentService.update(id, dto));
    }

    @DeleteMapping("/api/admin/treatments/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        treatmentService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
