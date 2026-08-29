package com.mariayasmim.estetica.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Service
public class GeminiService {
    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final String apiKey;
    private final String model;

    private static final String SYSTEM_PROMPT = """
        Você é a assistente virtual da clínica Maria Yasmim Lopes Estética, em Taboão da Serra.
        Responda sempre em português do Brasil, com simpatia, de forma curta e profissional.
        Ajude com dúvidas sobre tratamentos, cuidados estéticos e como agendar.
        Não invente preços, horários, resultados médicos ou informações que não foram fornecidas.
        Para marcar um horário, oriente a pessoa a usar a seção 'Agendamento' do próprio site.
        Não dê diagnóstico médico. Em caso de dúvida clínica importante, recomende procurar um profissional de saúde.
        """;

    public GeminiService(ObjectMapper objectMapper,
                         @Value("${gemini.api-key:}") String apiKey,
                         @Value("${gemini.model:gemini-3.6-flash}") String model) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    }

    public String chat(String message) {
        if (apiKey == null || apiKey.isBlank()) {
            return "O assistente virtual ainda não foi configurado. Tente novamente mais tarde.";
        }
        try {
            Map<String, Object> body = Map.of(
                    "system_instruction", Map.of("parts", new Object[]{Map.of("text", SYSTEM_PROMPT)}),
                    "contents", new Object[]{Map.of("role", "user", "parts", new Object[]{Map.of("text", message)})}
            );
            String json = objectMapper.writeValueAsString(body);
            URI uri = URI.create("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent");
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(30))
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .header("x-goog-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = null;
            int maxAttempts = 3;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() / 100 == 2) {
                    break;
                }
                boolean retryable = response.statusCode() == 503 || response.statusCode() == 429;
                log.error("Gemini respondeu HTTP {} (tentativa {}/{}) - body: {}",
                        response.statusCode(), attempt, maxAttempts, response.body());
                if (!retryable || attempt == maxAttempts) {
                    throw new IllegalStateException("Gemini respondeu HTTP " + response.statusCode());
                }
                Thread.sleep(500L * attempt); // backoff simples: 0.5s, 1s
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode text = root.path("candidates").path(0).path("content").path("parts").path(0).path("text");
            if (text.isMissingNode() || text.asText().isBlank()) {
                log.warn("Resposta do Gemini sem texto utilizavel. Body: {}", response.body());
                return "Desculpe, não consegui responder agora. Pode tentar novamente?";
            }
            return text.asText();
        } catch (Exception e) {
            log.error("Falha ao chamar a API do Gemini", e);
            return "Desculpe, estou com uma instabilidade momentânea. Tente novamente em alguns instantes.";
        }
    }
}