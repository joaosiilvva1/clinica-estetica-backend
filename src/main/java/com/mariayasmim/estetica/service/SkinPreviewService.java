package com.mariayasmim.estetica.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class SkinPreviewService {

    private static final Logger log = LoggerFactory.getLogger(SkinPreviewService.class);
    private static final String PROMPT = "Create one photorealistic, illustrative preview using the provided face photo. "
            + "Keep the same adult person, identity, facial features, face shape, skin tone, age, expression, pose, crop, background and lighting. "
            + "Make only a subtle and plausible cosmetic skin-cleanliness/brightness refinement that could be associated with a facial cleansing. "
            + "Preserve natural pores, texture, freckles, moles, fine lines and any defining marks. Do not beautify, make the skin flawless, "
            + "change makeup, remove permanent marks, alter facial features, diagnose a condition, or imply a guaranteed treatment outcome. "
            + "This is a hypothetical cosmetic visualization, not a medical prediction. Return the edited portrait image only.";

    private final RestClient restClient;
    private final String apiKey;
    private final String model;

    public SkinPreviewService(
            @Value("${gemini.api-key:}") String apiKey,
            @Value("${gemini.image-model:gemini-3.1-flash-image}") String model
    ) {
        this.apiKey = apiKey;
        this.model = model;
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(90_000);
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .requestFactory(requestFactory)
                .build();
    }

    public GeneratedImage generate(byte[] photoBytes, String mimeType) {
        if (apiKey == null || apiKey.isBlank()) {
            log.error("GEMINI_API_KEY não configurada — prévia de pele indisponível.");
            throw unavailable();
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(Map.of("role", "user", "parts", List.of(
                        Map.of("text", PROMPT),
                        Map.of("inline_data", Map.of(
                                "mime_type", mimeType,
                                "data", Base64.getEncoder().encodeToString(photoBytes)
                        ))
                ))),
                "generationConfig", Map.of("responseModalities", List.of("IMAGE"))
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri("/models/{model}:generateContent", model)
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);
            return extractImage(response);
        } catch (RestClientResponseException e) {
            log.error("Gemini image preview failed with HTTP {} for model {}", e.getStatusCode().value(), model);
            throw unavailable();
        } catch (RestClientException e) {
            log.error("Gemini image preview connection failed for model {}", model);
            throw unavailable();
        } catch (RuntimeException e) {
            if (e instanceof ResponseStatusException statusException) throw statusException;
            log.error("Gemini returned an invalid image preview response for model {}", model);
            throw unavailable();
        }
    }

    private GeneratedImage extractImage(Map<?, ?> response) {
        if (response != null && response.get("candidates") instanceof List<?> candidates && !candidates.isEmpty()) {
            Object first = candidates.get(0);
            if (first instanceof Map<?, ?> candidate && candidate.get("content") instanceof Map<?, ?> content
                    && content.get("parts") instanceof List<?> parts) {
                for (Object part : parts) {
                    if (!(part instanceof Map<?, ?> partMap)) continue;
                    Object inlineData = partMap.containsKey("inlineData") ? partMap.get("inlineData") : partMap.get("inline_data");
                    if (inlineData instanceof Map<?, ?> image) {
                        Object data = image.get("data");
                        Object type = image.containsKey("mimeType") ? image.get("mimeType") : image.get("mime_type");
                        if (data instanceof String base64 && !base64.isBlank()
                                && type instanceof String mime && mime.startsWith("image/")) {
                            return new GeneratedImage(base64, mime);
                        }
                    }
                }
            }
        }
        log.warn("Gemini image generation returned no image candidate.");
        throw unavailable();
    }

    private ResponseStatusException unavailable() {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "A simulação está indisponível no momento. Tente novamente em instantes.");
    }

    public record GeneratedImage(String imageBase64, String mimeType) {}
}
