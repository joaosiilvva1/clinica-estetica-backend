package com.mariayasmim.estetica.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/** Limite efêmero para reduzir abuso do endpoint público de geração de imagens. */
@Component
public class SkinPreviewRateLimiter {

    private static final int MAX_REQUESTS_PER_WINDOW = 3;
    private static final long WINDOW_MILLIS = Duration.ofHours(1).toMillis();
    private static final int MAX_TRACKED_CLIENTS = 10_000;
    private final ConcurrentHashMap<String, Window> clients = new ConcurrentHashMap<>();

    public boolean allow(String clientKey, long now) {
        if (clients.size() >= MAX_TRACKED_CLIENTS) {
            clients.entrySet().removeIf(entry -> now - entry.getValue().startedAt() >= WINDOW_MILLIS);
            if (clients.size() >= MAX_TRACKED_CLIENTS) return false;
        }
        Window window = clients.compute(clientKey, (key, current) -> {
            if (current == null || now - current.startedAt() >= WINDOW_MILLIS) return new Window(now, 1);
            return new Window(current.startedAt(), current.requests() + 1);
        });
        return window.requests() <= MAX_REQUESTS_PER_WINDOW;
    }

    private record Window(long startedAt, int requests) {}
}
