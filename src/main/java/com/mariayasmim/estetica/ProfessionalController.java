package com.mariayasmim.estetica.controller;

import com.mariayasmim.estetica.dto.ProfessionalDTO;
import com.mariayasmim.estetica.entity.User;
import com.mariayasmim.estetica.enums.Role;
import com.mariayasmim.estetica.exception.ResourceNotFoundException;
import com.mariayasmim.estetica.service.com.mariayasmim.estetica.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * MVP: clínica com profissional única (Maria Yasmim), que também é o usuário ADMIN.
 * Não existe cadastro de "profissional" separado do usuário — se a clínica crescer para
 * várias profissionais, isso precisa virar um recurso próprio (Fase 3, junto com a
 * Disponibilidade por dia da semana já sinalizada em AppointmentService).
 */
@RestController
@RequiredArgsConstructor
public class ProfessionalController {

    private final UserRepository userRepository;

    @GetMapping("/api/professionals/public")
    public ResponseEntity<List<ProfessionalDTO>> listPublic() {
        User professional = userRepository.findFirstByRoleOrderByNameAsc(Role.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhuma profissional cadastrada."));
        return ResponseEntity.ok(List.of(ProfessionalDTO.fromEntity(professional)));
    }
}