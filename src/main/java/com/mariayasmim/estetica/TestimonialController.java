package com.mariayasmim.estetica;

import com.mariayasmim.estetica.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class TestimonialController {

    @Autowired
    private TestimonialRepository repository;

    // --- Público: cliente envia o depoimento (fica pendente até a admin aprovar) ---

    @PostMapping("/api/testimonials/public")
    public ResponseEntity<Testimonial> salvarDepoimento(@RequestBody Testimonial testimonial) {
        // Nunca confia em "approved" vindo do cliente: todo depoimento novo nasce pendente.
        testimonial.setId(null);
        testimonial.setApproved(false);
        Testimonial salvo = repository.save(testimonial);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping("/api/testimonials/public")
    public ResponseEntity<List<Testimonial>> listarDepoimentos() {
        // Só mostra no site os depoimentos já aprovados pela administradora.
        return ResponseEntity.ok(repository.findByApprovedTrue());
    }

    // --- Administrativo: moderação, protegido por ROLE_ADMIN em SecurityConfig ---

    @GetMapping("/api/admin/testimonials/pending")
    public ResponseEntity<List<Testimonial>> listarPendentes() {
        return ResponseEntity.ok(repository.findByApprovedFalseOrderByIdDesc());
    }

    @PatchMapping("/api/admin/testimonials/{id}/approve")
    public ResponseEntity<Testimonial> aprovarDepoimento(@PathVariable Long id) {
        Testimonial testimonial = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Depoimento não encontrado."));
        testimonial.setApproved(true);
        return ResponseEntity.ok(repository.save(testimonial));
    }

    @DeleteMapping("/api/admin/testimonials/{id}")
    public ResponseEntity<Void> rejeitarDepoimento(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}