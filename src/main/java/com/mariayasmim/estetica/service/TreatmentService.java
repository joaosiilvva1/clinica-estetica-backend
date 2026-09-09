package com.mariayasmim.estetica.service;

import com.mariayasmim.estetica.dto.TreatmentRequestDTO;
import com.mariayasmim.estetica.dto.TreatmentResponseDTO;
import com.mariayasmim.estetica.entity.Treatment;
import com.mariayasmim.estetica.exception.ResourceNotFoundException;
import com.mariayasmim.estetica.exception.TreatmentStillActiveException;
import com.mariayasmim.estetica.repository.AppointmentRepository;
import com.mariayasmim.estetica.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final AppointmentRepository appointmentRepository;

    public List<TreatmentResponseDTO> listActive() {
        return treatmentRepository.findByActiveTrue().stream()
                .map(TreatmentResponseDTO::from)
                .collect(Collectors.toList());
    }

    public List<TreatmentResponseDTO> listAll() {
        return treatmentRepository.findAll().stream()
                .map(TreatmentResponseDTO::from)
                .collect(Collectors.toList());
    }

    public TreatmentResponseDTO create(TreatmentRequestDTO dto) {
        Treatment treatment = Treatment.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .durationMinutes(dto.getDurationMinutes())
                .active(true)
                .build();
        return TreatmentResponseDTO.from(treatmentRepository.save(treatment));
    }

    public TreatmentResponseDTO update(UUID id, TreatmentRequestDTO dto) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamento não encontrado."));
        treatment.setName(dto.getName());
        treatment.setDescription(dto.getDescription());
        treatment.setPrice(dto.getPrice());
        treatment.setDurationMinutes(dto.getDurationMinutes());
        return TreatmentResponseDTO.from(treatmentRepository.save(treatment));
    }

    // Em vez de delete: desativa. Usado quando o tratamento ainda pode
    // reaparecer/ser reativado depois.
    public TreatmentResponseDTO setActive(UUID id, boolean active) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamento não encontrado."));
        treatment.setActive(active);
        return TreatmentResponseDTO.from(treatmentRepository.save(treatment));
    }

    // Exclusão definitiva: só permitida com o tratamento já inativo (ver
    // TreatmentController). Decisão do negócio: apaga em cascata os
    // agendamentos vinculados a esse tratamento — a cliente perde o
    // histórico desses atendimentos, de propósito, não é bug.
    // A ordem importa: appointments primeiro, treatment depois, dentro da
    // mesma transação, senão um erro no meio deixa agendamento órfão
    // apontando pra um tratamento que não existe mais.
    @Transactional
    public void delete(UUID id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamento não encontrado."));
        if (treatment.isActive()) {
            throw new TreatmentStillActiveException(
                    "Desative o tratamento antes de excluí-lo.");
        }
        appointmentRepository.deleteByTreatmentId(treatment.getId());
        treatmentRepository.delete(treatment);
    }
}