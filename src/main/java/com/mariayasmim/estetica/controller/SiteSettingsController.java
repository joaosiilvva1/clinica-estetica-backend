package com.mariayasmim.estetica.controller;

import com.mariayasmim.estetica.dto.SiteSettingsDTO;
import com.mariayasmim.estetica.service.SiteSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class SiteSettingsController {

    private final SiteSettingsService siteSettingsService;

    // --- Público: textos institucionais e contato, pra home do site ---
    @GetMapping("/api/site-settings/public")
    public ResponseEntity<SiteSettingsDTO> getPublic() {
        return ResponseEntity.ok(siteSettingsService.get());
    }

    // --- Administrativo: protegido por ROLE_ADMIN em SecurityConfig ---
    @GetMapping("/api/admin/site-settings")
    public ResponseEntity<SiteSettingsDTO> getAdmin() {
        return ResponseEntity.ok(siteSettingsService.get());
    }

    @PutMapping("/api/admin/site-settings")
    public ResponseEntity<SiteSettingsDTO> update(@Valid @RequestBody SiteSettingsDTO dto) {
        return ResponseEntity.ok(siteSettingsService.update(dto));
    }
}