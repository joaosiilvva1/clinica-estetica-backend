package com.mariayasmim.estetica.service.com.mariayasmim.estetica;

import com.mariayasmim.estetica.entity.User;
import com.mariayasmim.estetica.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // MVP: clínica com profissional única — essa profissional é o próprio usuário ADMIN.
    // Usado pelo endpoint público /api/professionals/public para o cliente final descobrir
    // o professionalId sem precisar de login.
    Optional<User> findFirstByRoleOrderByNameAsc(Role role);
}