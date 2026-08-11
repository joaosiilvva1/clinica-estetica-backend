package com.mariayasmim.estetica.entity;

import com.mariayasmim.estetica.enums.AppointmentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Prevenção de double-booking / overlap, em camadas:
 * 1) Aplicação (defesa principal): AppointmentService adquire lock pessimista
 *    (PESSIMISTIC_WRITE) sobre agendamentos do profissional no intervalo pretendido
 *    e verifica sobreposição ANTES de inserir, dentro da mesma transação.
 * 2) DB (rede de segurança): UNIQUE(professional_id, scheduled_at) barra o caso de
 *    duas requisições caindo no mesmo instante exato — não cobre overlap de duração,
 *    que é responsabilidade da camada 1.
 * @Version cobre updates concorrentes do MESMO registro (ex.: dois cancelamentos
 * simultâneos), problema diferente e complementar aos dois acima.
 *
 * O cliente final não possui conta neste MVP — dados de contato são capturados
 * diretamente no agendamento.
 */
@Entity
@Table(
        name = "appointments",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_professional_slot",
                columnNames = {"professional_id", "scheduled_at"}
        ),
        indexes = {
                @Index(name = "idx_appointment_professional_time", columnList = "professional_id, scheduled_at"),
                @Index(name = "idx_appointment_client_whatsapp", columnList = "client_whatsapp")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "client_name", nullable = false, length = 150)
    private String clientName;

    @NotBlank
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "WhatsApp deve conter apenas dígitos (com DDI/DDD opcional com '+')")
    @Column(name = "client_whatsapp", nullable = false, length = 20)
    private String clientWhatsapp;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "professional_id", nullable = false)
    private User professional;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @NotNull
    @Future
    @Column(name = "scheduled_at", nullable = false)
    private Instant scheduledAt;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(length = 300)
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Long version;

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
        if (this.durationMinutes == null && this.treatment != null) {
            this.durationMinutes = this.treatment.getDurationMinutes();
        }
    }

    @Transient
    public Instant getEndsAt() {
        return this.scheduledAt.plusSeconds(this.durationMinutes * 60L);
    }
}
