package com.mariayasmim.estetica.controller;

import com.mariayasmim.estetica.dto.ChatRequestDTO;
import com.mariayasmim.estetica.dto.ChatResponseDTO;
import com.mariayasmim.estetica.service.GroqService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    private final GroqService groqService;
    public ChatController(GroqService groqService) { this.groqService = groqService; }
    @PostMapping("/public")
    public ResponseEntity<ChatResponseDTO> chat(@Valid @RequestBody ChatRequestDTO request) {
        return ResponseEntity.ok(new ChatResponseDTO(groqService.chat(request.message())));
    }
}