package com.mariayasmim.estetica.repository;

import com.mariayasmim.estetica.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {
    List<Treatment> findByActiveTrue();
}
