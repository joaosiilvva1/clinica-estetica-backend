package com.mariayasmim.estetica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

/**
 * Cliente HTTP para a Gemini API (generateContent). Antes deste arquivo não existia:
 * havia um GeminiService referenciado pelo ChatController mas nunca implementado (a classe
 * foi apagada numa tentativa de migrar para Groq, e o arquivo que deveria virar GroqService
 * acabou com uma cópia do próprio ChatController colada dentro por engano — duas classes
 * ChatController no mesmo pacote, projeto não compilava).
 *
 * Falhas de rede/da API do Gemini são absorvidas aqui e viram uma mensagem de fallback,
 * para que o endpoint público de chat sempre responda 200 com algo utilizável em vez de
 * estourar 500 pro usuário final quando o Gemini está fora do ar ou a chave é inválida.
 */
@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);
    private static final String FALLBACK_REPLY =
            "Desculpe, não consegui responder agora. Tente novamente em instantes ou fale direto com a Maria Yasmim pelo WhatsApp.";

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public GeminiService(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.model:gemini-3.7-flash}") String model
    ) {
        this.apiKey = apiKey;
        this.model = model;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5_000);
        requestFactory.setReadTimeout(15_000);

        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .requestFactory(requestFactory)
                .build();
    }

    public String chat(String message) {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("GEMINI_API_KEY não configurada — chat público indisponível.");
            return FALLBACK_REPLY;
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
                                            + "Se perguntarem sobre agendamento, oriente a usar o formulário do site."
                            ))
                    )
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.post()
                    .uri("/models/{model}:generateContent?key={apiKey}", model, apiKey)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            return extractText(response);
        } catch (RestClientException e) {
            log.error("Falha ao chamar a Gemini API", e);
            return FALLBACK_REPLY;
        } catch (RuntimeException e) {
            log.error("Resposta inesperada da Gemini API", e);
            return FALLBACK_REPLY;
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {
        if (response == null) {
            return FALLBACK_REPLY;
        }

        List<Object> candidates = (List<Object>) response.get("candidates");
        if (candidates == null || candidates.isEmpty()) {
            log.warn("Gemini API não retornou candidates (possível bloqueio de safety filter).");
            return FALLBACK_REPLY;
        }

        Map<String, Object> firstCandidate = (Map<String, Object>) candidates.get(0);
        Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
        if (content == null) {
            return FALLBACK_REPLY;
        }

        List<Object> parts = (List<Object>) content.get("parts");
        if (parts == null || parts.isEmpty()) {
            return FALLBACK_REPLY;
        }

        Map<String, Object> firstPart = (Map<String, Object>) parts.get(0);
        Object text = firstPart.get("text");
        return text != null ? text.toString().trim() : FALLBACK_REPLY;
    }
}