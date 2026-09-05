package com.mariayasmim.estetica.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limit simples em memória para /api/auth/login, por IP. Existe só uma conta (ADMIN),
 * pública e sem esse limite antes — dava pra tentar senha indefinidamente sem bloqueio.
 *
 * Best-effort: em memória (zera se a instância reiniciar) e por instância (não é
 * distribuído). Suficiente para o tier gratuito do Render, que roda uma única instância.
 * Se a clínica crescer e o Render escalar horizontalmente, isso precisa virar algo
 * compartilhado (ex.: Redis).
 */
@Component
public class LoginRateLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MILLIS = 15 * 60 * 1000L; // 15 minutos

    private record Window(AtomicInteger count, Instant windowStart) {
    }

    private final ConcurrentHashMap<String, Window> attemptsByIp = new ConcurrentHashMap<>();

    public boolean isBlocked(String ip) {
        Window window = attemptsByIp.get(ip);
        if (window == null) {
            return false;
        }
        if (windowExpired(window)) {
            attemptsByIp.remove(ip, window);
            return false;
        }
        return window.count().get() >= MAX_ATTEMPTS;
    }

    public void registerFailedAttempt(String ip) {
        attemptsByIp.compute(ip, (key, existing) -> {
            if (existing == null || windowExpired(existing)) {
                return new Window(new AtomicInteger(1), Instant.now());
            }
            existing.count().incrementAndGet();
            return existing;
        });
    }

    public void registerSuccess(String ip) {
        attemptsByIp.remove(ip);
    }

    private boolean windowExpired(Window window) {
        return Instant.now().toEpochMilli() - window.windowStart().toEpochMilli() > WINDOW_MILLIS;
    }
}