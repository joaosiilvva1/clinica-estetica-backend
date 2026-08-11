package com.mariayasmim.estetica.dto;

import com.mariayasmim.estetica.entity.Appointment;
import com.mariayasmim.estetica.enums.AppointmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDTO {

    private UUID id;
    private String clientName;
    private String clientWhatsapp;

    private UUID professionalId;
    private String professionalName;

    private UUID treatmentId; // Atualizado para UUID para casar com a entidade Treatment
    private String treatmentName;

    private Instant scheduledAt;
    private Instant endsAt;
    private Integer durationMinutes;
    private AppointmentStatus status;
    private String notes;
    private Instant createdAt;

    public static AppointmentResponseDTO fromEntity(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .clientName(appointment.getClientName())
                .clientWhatsapp(appointment.getClientWhatsapp())
                .professionalId(appointment.getProfessional() != null ? appointment.getProfessional().getId() : null)
                .professionalName(appointment.getProfessional() != null ? appointment.getProfessional().getName() : null)
                .treatmentId(appointment.getTreatment() != null ? appointment.getTreatment().getId() : null)
                .treatmentName(appointment.getTreatment() != null ? appointment.getTreatment().getName() : null)
                .scheduledAt(appointment.getScheduledAt())
                .endsAt(appointment.getEndsAt())
                .durationMinutes(appointment.getDurationMinutes())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .build();
    }
}