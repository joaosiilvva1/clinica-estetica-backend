package com.mariayasmim.estetica.controller;

import com.mariayasmim.estetica.dto.AppointmentRequestDTO;
import com.mariayasmim.estetica.dto.AppointmentResponseDTO;
import com.mariayasmim.estetica.enums.AppointmentStatus;
import com.mariayasmim.estetica.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // --- Público: fluxo de agendamento da cliente final (sem conta) ---

    @PostMapping("/api/appointments/public")
    public ResponseEntity<AppointmentResponseDTO> createPublicAppointment(
            @Valid @RequestBody AppointmentRequestDTO dto) {
        AppointmentResponseDTO created = appointmentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/api/appointments/public/available-slots")
    public ResponseEntity<List<Instant>> getAvailableSlots(
            @RequestParam UUID professionalId,
            @RequestParam UUID treatmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(professionalId, treatmentId, date));
    }

    // --- Administrativo: agenda da profissional, protegido por ROLE_ADMIN em SecurityConfig ---

    @GetMapping("/api/admin/appointments")
    public ResponseEntity<List<AppointmentResponseDTO>> listByProfessionalAndDate(
            @RequestParam UUID professionalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentService.listByProfessionalAndDate(professionalId, date));
    }

    @PatchMapping("/api/admin/appointments/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateStatus(
            @PathVariable UUID id, @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }
}
