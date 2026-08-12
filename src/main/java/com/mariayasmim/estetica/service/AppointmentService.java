package com.mariayasmim.estetica.service;

import com.mariayasmim.estetica.dto.AppointmentRequestDTO;
import com.mariayasmim.estetica.dto.AppointmentResponseDTO;
import com.mariayasmim.estetica.entity.Appointment;
import com.mariayasmim.estetica.entity.Treatment;
import com.mariayasmim.estetica.entity.User;
import com.mariayasmim.estetica.enums.AppointmentStatus;
import com.mariayasmim.estetica.exception.ResourceNotFoundException;
import com.mariayasmim.estetica.exception.SlotUnavailableException;
import com.mariayasmim.estetica.repository.AppointmentRepository;
import com.mariayasmim.estetica.repository.TreatmentRepository;
import com.mariayasmim.estetica.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final TreatmentRepository treatmentRepository;
    private final UserRepository userRepository;
    private final AdvisoryLockService advisoryLockService;

    // Stopgap: horário comercial fixo, único para toda a clínica. Não existe entidade de
    // Disponibilidade — se a clínica precisar de horários variáveis por dia da semana ou
    // por profissional, isso precisa virar uma tabela própria (Fase 3).
    @Value("${business.opening-hour:9}")
    private int openingHour;

    @Value("${business.closing-hour:18}")
    private int closingHour;

    @Value("${business.zone-id:America/Sao_Paulo}")
    private String zoneIdConfig;

    @Value("${business.slot-granularity-minutes:30}")
    private int slotGranularityMinutes;

    /**
     * Defesa principal contra overlap: advisory lock por profissional (ver
     * AdvisoryLockService), adquirido ANTES da checagem de overlap. Isso serializa toda
     * tentativa de agendamento para o mesmo profissional, inclusive a "corrida pelo
     * primeiro horário" que um FOR UPDATE sozinho não cobre (nada para travar se ainda não
     * existe conflito gravado). A UNIQUE constraint em Appointment continua como rede de
     * segurança redundante para start exato coincidente, mas não é mais a defesa real.
     * Isolation READ_COMMITTED é suficiente porque quem serializa agora é o advisory lock,
     * não o nível de isolamento da transação.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AppointmentResponseDTO create(AppointmentRequestDTO dto) {
        Treatment treatment = treatmentRepository.findById(dto.treatmentId())
                .filter(Treatment::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamento não encontrado ou inativo"));

        User professional = userRepository.findById(dto.professionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profissional não encontrado"));

        Instant windowStart = dto.scheduledAt();
        Instant windowEnd = windowStart.plusSeconds(treatment.getDurationMinutes() * 60L);

        validateWithinBusinessHours(windowStart, windowEnd);

        // Bloqueia até que nenhuma outra transação esteja no meio de um create() para este
        // mesmo profissional. Liberado automaticamente no commit/rollback.
        advisoryLockService.lockProfessionalSchedule(professional.getId());

        // Com o advisory lock já adquirido, esta leitura agora é confiável: se vier vazia,
        // não existe (e não vai aparecer) conflito concorrente para este profissional.
        List<Appointment> conflicts = appointmentRepository.findOverlapping(
                professional.getId(), windowStart, windowEnd
        );

        if (!conflicts.isEmpty()) {
            throw new SlotUnavailableException("Horário indisponível para este profissional.");
        }

        Appointment appointment = Appointment.builder()
                .clientName(dto.clientName())
                .clientWhatsapp(dto.clientWhatsapp())
                .professional(professional)
                .treatment(treatment)
                .scheduledAt(windowStart)
                .durationMinutes(treatment.getDurationMinutes())
                .status(AppointmentStatus.PENDING)
                .notes(dto.notes())
                .build();

        // A constraint UNIQUE(professional_id, scheduled_at) do banco ainda pode disparar
        // DataIntegrityViolationException aqui numa corrida de start exato coincidente —
        // tratado no GlobalExceptionHandler como 409, redundante mas inofensivo com o lock acima.
        Appointment saved = appointmentRepository.save(appointment);
        return AppointmentResponseDTO.fromEntity(saved);
    }

    @Transactional
    public AppointmentResponseDTO updateStatus(UUID appointmentId, AppointmentStatus newStatus) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado"));
        appointment.setStatus(newStatus);
        // @Version garante 409 automático (OptimisticLockException) se outra requisição
        // alterou este mesmo agendamento entre o find e o save.
        return AppointmentResponseDTO.fromEntity(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> listByProfessionalAndDate(UUID professionalId, LocalDate date) {
        ZoneId zone = ZoneId.of(zoneIdConfig);
        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();

        return appointmentRepository
                .findByProfessionalIdAndScheduledAtBetweenAndStatusNotOrderByScheduledAt(
                        professionalId, dayStart, dayEnd, AppointmentStatus.CANCELLED
                )
                .stream()
                .map(AppointmentResponseDTO::fromEntity)
                .toList();
    }

    /**
     * Gera slots livres do dia varrendo o horário comercial em passos de
     * slot-granularity-minutes e descartando os que colidem com agendamentos existentes
     * ou com a duração do tratamento pedido. Não tem lock (é só leitura informativa) —
     * o lock de verdade acontece em create(). Portanto isto é best-effort: o slot pode
     * ser tomado entre a consulta e a confirmação, e o cliente recebe 409 nesse caso.
     */
    @Transactional(readOnly = true)
    public List<Instant> getAvailableSlots(UUID professionalId, UUID treatmentId, LocalDate date) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .filter(Treatment::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamento não encontrado ou inativo"));

        ZoneId zone = ZoneId.of(zoneIdConfig);
        Instant dayStart = date.atStartOfDay(zone).toInstant();
        Instant dayEnd = date.plusDays(1).atStartOfDay(zone).toInstant();

        List<Appointment> existing = appointmentRepository
                .findByProfessionalIdAndScheduledAtBetweenAndStatusNotOrderByScheduledAt(
                        professionalId, dayStart, dayEnd, AppointmentStatus.CANCELLED
                );

        Instant openInstant = date.atTime(LocalTime.of(openingHour, 0)).atZone(zone).toInstant();
        Instant closeInstant = date.atTime(LocalTime.of(closingHour, 0)).atZone(zone).toInstant();
        long treatmentSeconds = treatment.getDurationMinutes() * 60L;

        List<Instant> freeSlots = new ArrayList<>();
        Instant cursor = openInstant;

        while (!cursor.plusSeconds(treatmentSeconds).isAfter(closeInstant)) {
            Instant candidateEnd = cursor.plusSeconds(treatmentSeconds);
            Instant finalCursor = cursor;
            boolean overlaps = existing.stream().anyMatch(a ->
                    finalCursor.isBefore(a.getEndsAt()) && candidateEnd.isAfter(a.getScheduledAt())
            );
            if (!overlaps && cursor.isAfter(Instant.now())) {
                freeSlots.add(cursor);
            }
            cursor = cursor.plusSeconds(slotGranularityMinutes * 60L);
        }

        return freeSlots;
    }

    private void validateWithinBusinessHours(Instant start, Instant end) {
        ZoneId zone = ZoneId.of(zoneIdConfig);
        ZonedDateTime startZoned = start.atZone(zone);
        ZonedDateTime endZoned = end.atZone(zone);

        boolean withinHours = startZoned.toLocalTime().getHour() >= openingHour
                && !endZoned.toLocalTime().isAfter(LocalTime.of(closingHour, 0))
                && startZoned.toLocalDate().equals(endZoned.toLocalDate());

        if (!withinHours) {
            throw new SlotUnavailableException(
                    "Horário fora do funcionamento (%02d:00–%02d:00).".formatted(openingHour, closingHour)
            );
        }
    }
}
