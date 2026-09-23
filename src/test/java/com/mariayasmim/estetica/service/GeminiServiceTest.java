package com.mariayasmim.estetica.service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class GeminiServiceTest {
    @Test void missingKeyIsUnavailable() {
        var service = new GeminiService(RestClient.create(), "", "test-model");
        assertEquals(503, assertThrows(ResponseStatusException.class, () -> service.chat("Olá")).getStatusCode().value());
    }
    @Test void sendsKeyInHeaderAndUsesManualBooking() {
        var builder = RestClient.builder().baseUrl("https://example.test");
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new GeminiService(builder.build(), "test-key", "test-model");
        server.expect(requestTo("https://example.test/models/test-model:generateContent"))
            .andExpect(header("x-goog-api-key", "test-key"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("5511916224612")))
            .andRespond(withSuccess("{\"candidates\":[{\"content\":{\"parts\":[{\"text\":\"Fale com a Maria.\"}]}}]}", MediaType.APPLICATION_JSON));
        assertEquals("Fale com a Maria.", service.chat("Como agendar?")); server.verify();
    }
    @Test void providerErrorIsNotReportedAsSuccess() {
        var builder = RestClient.builder().baseUrl("https://example.test");
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new GeminiService(builder.build(), "test-key", "test-model");
        server.expect(requestTo("https://example.test/models/test-model:generateContent"))
            .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertEquals(503, assertThrows(ResponseStatusException.class, () -> service.chat("Olá")).getStatusCode().value());
        server.verify();
    }
    @Test void emptyModelReplyIsUnavailable() {
        var builder = RestClient.builder().baseUrl("https://example.test");
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new GeminiService(builder.build(), "test-key", "test-model");
        server.expect(requestTo("https://example.test/models/test-model:generateContent"))
            .andRespond(withSuccess("{\"candidates\":[]}", MediaType.APPLICATION_JSON));
        assertEquals(503, assertThrows(ResponseStatusException.class, () -> service.chat("Olá")).getStatusCode().value());
        server.verify();
    }
}
