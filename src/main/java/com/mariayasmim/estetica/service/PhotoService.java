package com.mariayasmim.estetica.service;

import com.mariayasmim.estetica.dto.PhotoRequestDTO;
import com.mariayasmim.estetica.dto.PhotoResponseDTO;
import com.mariayasmim.estetica.entity.Photo;
import com.mariayasmim.estetica.exception.ResourceNotFoundException;
import com.mariayasmim.estetica.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public List<PhotoResponseDTO> listActive() {
        return photoRepository.findByActiveTrueOrderBySortOrderAsc().stream()
                .map(PhotoResponseDTO::from)
                .collect(Collectors.toList());
    }

    public List<PhotoResponseDTO> listAll() {
        return photoRepository.findAllByOrderBySortOrderAsc().stream()
                .map(PhotoResponseDTO::from)
                .collect(Collectors.toList());
    }

    public PhotoResponseDTO create(PhotoRequestDTO dto) {
        int nextOrder = dto.getSortOrder() != null
                ? dto.getSortOrder()
                : photoRepository.findAllByOrderBySortOrderAsc().size();

        Photo photo = Photo.builder()
                .url(dto.getUrl())
                .title(dto.getTitle())
                .sortOrder(nextOrder)
                .active(true)
                .build();
        return PhotoResponseDTO.from(photoRepository.save(photo));
    }

    public PhotoResponseDTO update(UUID id, PhotoRequestDTO dto) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada."));
        photo.setUrl(dto.getUrl());
        photo.setTitle(dto.getTitle());
        if (dto.getSortOrder() != null) {
            photo.setSortOrder(dto.getSortOrder());
        }
        return PhotoResponseDTO.from(photoRepository.save(photo));
    }

    public PhotoResponseDTO setActive(UUID id, boolean active) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto não encontrada."));
        photo.setActive(active);
        return PhotoResponseDTO.from(photoRepository.save(photo));
    }

    public void delete(UUID id) {
        if (!photoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Foto não encontrada.");
        }
        photoRepository.deleteById(id);
    }
}