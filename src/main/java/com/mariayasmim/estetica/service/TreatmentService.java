package com.mariayasmim.estetica.service;

import com.mariayasmim.estetica.dto.TreatmentRequestDTO;
import com.mariayasmim.estetica.dto.TreatmentResponseDTO;
import com.mariayasmim.estetica.entity.Treatment;
import com.mariayasmim.estetica.exception.ResourceNotFoundException;
import com.mariayasmim.estetica.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;

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

    // Em vez de delete: desativa. Tratamentos já usados em agendamentos não podem sumir da tabela.
    public TreatmentResponseDTO setActive(UUID id, boolean active) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tratamento não encontrado."));
        treatment.setActive(active);
        return TreatmentResponseDTO.from(treatmentRepository.save(treatment));
    }
}
