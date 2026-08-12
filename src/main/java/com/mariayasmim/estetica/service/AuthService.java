package com.mariayasmim.estetica.service;

import com.mariayasmim.estetica.dto.AuthRequestDTO;
import com.mariayasmim.estetica.dto.AuthResponseDTO;
import com.mariayasmim.estetica.entity.User;
import com.mariayasmim.estetica.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Só existe login aqui, de propósito — não há endpoint de self-registration exposto.
 * Neste MVP o cliente final não tem conta (agenda informando nome/WhatsApp) e os únicos
 * User existentes são ADMIN/profissional, criados por seed/migração manual no banco, não
 * por uma rota pública. Se a clínica precisar cadastrar profissionais pelo app, isso tem
 * que ser uma rota protegida por ADMIN (Fase 3), nunca pública.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    public AuthResponseDTO login(AuthRequestDTO request) {
        User principal;
        try {
            var authResult = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
            principal = (User) authResult.getPrincipal();
        } catch (org.springframework.security.core.AuthenticationException e) {
            // Não vaza se foi "email não existe" vs "senha errada" — evita enumeração de contas.
            throw new BadCredentialsException("Credenciais inválidas");
        }

        String token = jwtService.generateToken(principal);
        return AuthResponseDTO.of(token, expirationMs);
    }
}
