package com.mariayasmim.estetica.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {
    @Test
    void preservesServiceUnavailableStatusFromGemini() {
        var handler = new GlobalExceptionHandler();
        var response = handler.handleResponseStatus(new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Assistente temporariamente indisponível."
        ));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals("Assistente temporariamente indisponível.", response.getBody().message());
    }
}
