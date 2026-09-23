package com.mariayasmim.estetica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

/** Cliente Gemini; falhas retornam indisponibilidade real, sem expor credenciais. */
@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);
    private static final String FALLBACK_REPLY =
            "Desculpe, não consegui responder agora. Tente novamente em instantes ou fale direto com a Maria Yasmim pelo WhatsApp.";

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    @Autowired
    public GeminiService(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-3.7-flash}") String model
    ) {
        this(createClient(), apiKey, model);
    }

    GeminiService(RestClient restClient, String apiKey, String model) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.model = model;
    }

    private static RestClient createClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        // Keep the model request alive long enough for the site's 90s cold-start timer.
        requestFactory.setReadTimeout(75_000);

        return RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .requestFactory(requestFactory)
                .build();
    }

    public String chat(String message) {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("GEMINI_API_KEY não configurada — chat público indisponível.");
            throw unavailable();
        }

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("role", "user", "parts", List.of(Map.of("text", message)))
                    ),
                    "systemInstruction", Map.of(
                            "parts", List.of(Map.of(
                                    "text", "Você é a assistente virtual da Maria Yasmim Lopes Estética. "
                                            + "Responda de forma breve, simpática e objetiva, em português. "
                                            + "O agendamento é manual pelo WhatsApp da Maria: 5511916224612. "
                                            + "Não existe formulário nem reserva automática de horário. "
                                            + "Não invente preços, disponibilidade, promoções ou informações da clínica. "
                                            + "Para esses detalhes, encaminhe ao WhatsApp. Não faça diagnósticos nem prescreva tratamentos."
                            ))
                    )
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return extractText(response);
        } catch (RestClientResponseException e) {
            // Nunca registre a chave, a pergunta da cliente ou o corpo da resposta.
            log.error("Gemini indisponível: HTTP {}, modelo {}", e.getStatusCode().value(), model);
            throw unavailable();
        } catch (RestClientException e) {
            log.error("Gemini: falha de conexão ou tempo limite, modelo {}", model);
            throw unavailable();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("Gemini: resposta inválida, modelo {}", model);
            throw unavailable();
        }
    }

    private ResponseStatusException unavailable() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, FALLBACK_REPLY);
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        if (response == null) {
            throw unavailable();
        }

        List<Object> candidates = (List<Object>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            log.warn("Gemini API não retornou candidates (possível bloqueio de safety filter).");
            throw unavailable();
        }

        Map<String, Object> firstCandidate = (Map<String, Object>) candidates.get(0);
        Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
        if (content == null) {
            throw unavailable();
        }

        List<Object> parts = (List<Object>) content.get("parts");
        if (parts == null || parts.isEmpty()) {
            throw unavailable();
        }

        Map<String, Object> firstPart = (Map<String, Object>) parts.get(0);
        Object text = firstPart.get("text");
        if (!(text instanceof String reply) || reply.isBlank()) {
            throw unavailable();
        }
        return reply.trim();
    }
}
