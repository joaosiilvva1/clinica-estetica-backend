package com.mariayasmim.estetica;

import com.mariayasmim.estetica.entity.Appointment;
import com.mariayasmim.estetica.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @PostMapping("/public")
    public ResponseEntity<?> createPublicAppointment(@RequestBody Map<String, Object> payload) {
        try {
            String clientName = (String) payload.get("clientName");
            String clientWhatsapp = (String) payload.get("clientWhatsapp");
            String scheduledAtStr = (String) payload.get("scheduledAt");

            Appointment appointment = new Appointment();
            appointment.setClientName(clientName);
            appointment.setClientWhatsapp(clientWhatsapp);
            appointment.setScheduledAt(OffsetDateTime.parse(scheduledAtStr));

            Appointment saved = appointmentRepository.save(appointment);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
