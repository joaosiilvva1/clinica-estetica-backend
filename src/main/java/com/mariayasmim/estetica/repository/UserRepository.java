package com.mariayasmim.estetica.repository;

import com.mariayasmim.estetica.entity.User;
import com.mariayasmim.estetica.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

 
    Optional<User> findFirstByRoleOrderByNameAsc(Role role);
}
