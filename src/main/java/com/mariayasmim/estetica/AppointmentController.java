package com.mariayasmim.estetica;

import com.mariayasmim.estetica.entity.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private com.mariayasmim.estetica.repository.AppointmentRepository appointmentRepository;

    @PostMapping("/public")
    public ResponseEntity<?> createPublicAppointment(@RequestBody Appointment appointment) {
        // 1. Salva o agendamento no banco de dados
        Appointment saved = appointmentRepository.save(appointment);

        // 2. Disparo automático ou notificação (pode ser integrado aqui futuramente)

        return ResponseEntity.ok(saved);
    }
}