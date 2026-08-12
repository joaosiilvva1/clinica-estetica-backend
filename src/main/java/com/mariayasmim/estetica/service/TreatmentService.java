package com.mariayasmim.estetica.service;

import com.mariayasmim.estetica.dto.TreatmentDTO;
import com.mariayasmim.estetica.entity.Treatment;
import com.mariayasmim.estetica.repository.TreatmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;

    public TreatmentService(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    @Transactional
    public TreatmentDTO create(TreatmentDTO dto) {
        Treatment treatment = dto.toEntity();
        Treatment saved = treatmentRepository.save(treatment);
        return TreatmentDTO.fromEntity(saved);
    }

    public List<TreatmentDTO> listAll() {
        return treatmentRepository.findAll().stream()
                .map(TreatmentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TreatmentDTO> listActive() {
        return treatmentRepository.findAll().stream()
                .filter(Treatment::isActive)
                .map(TreatmentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TreatmentDTO getById(UUID id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamento não encontrado com o ID: " + id));
        return TreatmentDTO.fromEntity(treatment);
    }

    @Transactional
    public TreatmentDTO update(UUID id, TreatmentDTO dto) {
        Treatment existing = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamento não encontrado com o ID: " + id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setDurationMinutes(dto.getDurationMinutes());
        if (dto.getActive() != null) {
            existing.setActive(dto.getActive());
        }

        Treatment updated = treatmentRepository.save(existing);
        return TreatmentDTO.fromEntity(updated);
    }

    @Transactional
    public void deactivate(UUID id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamento não encontrado com o ID: " + id));
        treatment.setActive(false);
        treatmentRepository.save(treatment);
    }

    @Transactional
    public void delete(UUID id) {
        if (!treatmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Tratamento não encontrado com o ID: " + id);
        }
        treatmentRepository.deleteById(id);
    }
}