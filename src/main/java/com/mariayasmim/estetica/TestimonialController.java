package com.mariayasmim.estetica;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testimonials")
@CrossOrigin(origins = "*")
public class TestimonialController {

    @Autowired
    private TestimonialRepository repository;

    @PostMapping("/public")
    public ResponseEntity<Testimonial> salvarDepoimento(@RequestBody Testimonial testimonial) {
        // Salva diretamente no PostgreSQL
        Testimonial salvo = repository.save(testimonial);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("/public")
    public ResponseEntity<List<Testimonial>> listarDepoimentos() {
        // Retorna todos os depoimentos salvos automaticamente para o site
        return ResponseEntity.ok(repository.findAll());
    }
}