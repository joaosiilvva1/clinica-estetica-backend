package com.mariayasmim.estetica.controller;

import com.mariayasmim.estetica.dto.ChatRequestDTO;
import com.mariayasmim.estetica.dto.ChatResponseDTO;
import com.mariayasmim.estetica.service.GeminiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final GeminiService geminiService;
    public ChatController(GeminiService geminiService) { this.geminiService = geminiService; }
    @PostMapping("/public")
    public ResponseEntity<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO request) {
        return ResponseEntity.ok(new ChatResponseDTO(geminiService.chat(request.message())));
    }
}
