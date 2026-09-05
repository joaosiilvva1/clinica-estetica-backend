package com.mariayasmim.estetica.controller;

import com.mariayasmim.estetica.dto.AuthRequestDTO;
import com.mariayasmim.estetica.dto.AuthResponseDTO;
import com.mariayasmim.estetica.security.LoginRateLimiter;
import com.mariayasmim.estetica.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiter loginRateLimiter;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request, HttpServletRequest httpRequest) {
        String ip = clientIp(httpRequest);

        if (loginRateLimiter.isBlocked(ip)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Muitas tentativas de login. Tente novamente em alguns minutos.");
        }

        try {
            AuthResponseDTO response = authService.login(request);
            loginRateLimiter.registerSuccess(ip);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            loginRateLimiter.registerFailedAttempt(ip);
            throw e;
        }
    }

    // Render/Vercel ficam atrás de proxy — X-Forwarded-For carrega o IP real do cliente
    // quando presente; sem proxy (dev local), cai no IP direto da conexão.
    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}