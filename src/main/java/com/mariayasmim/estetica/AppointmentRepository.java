package com.mariayasmim.estetica.repository;

import com.mariayasmim.estetica.entity.Appointment;
import com.mariayasmim.estetica.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Retorna os agendamentos do profissional que se SOBREPÕEM à janela
     * [windowStart, windowEnd), ignorando cancelados. Só é confiável quando chamada DEPOIS
     * de AdvisoryLockService.lockProfessionalSchedule(professionalId) na mesma transação —
     * sem o advisory lock, esta leitura sozinha não previne a corrida de dois inserts
     * concorrentes pro mesmo horário (ver AppointmentService.create).
     *
     * Nota: query nativa por causa da aritmética de intervalo do Postgres
     * (scheduled_at + duration_minutes minutos), não trivial em JPQL portável.
     */
    @Query(value = """
            SELECT * FROM appointments a
            WHERE a.professional_id = :professionalId
              AND a.status <> 'CANCELLED'
              AND a.scheduled_at < :windowEnd
              AND (a.scheduled_at + (a.duration_minutes * interval '1 minute')) > :windowStart
            """, nativeQuery = true)
    List<Appointment> findOverlapping(
            @Param("professionalId") UUID professionalId,
            @Param("windowStart") Instant windowStart,
            @Param("windowEnd") Instant windowEnd
    );

    List<Appointment> findByProfessionalIdAndScheduledAtBetweenAndStatusNotOrderByScheduledAt(
            UUID professionalId, Instant dayStart, Instant dayEnd, AppointmentStatus excludedStatus
    );
}
