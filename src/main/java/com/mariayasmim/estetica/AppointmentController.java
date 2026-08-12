package com.mariayasmim.estetica;

import com.mariayasmim.estetica.entity.Appointment;
import com.mariayasmim.estetica.entity.Professional;
import com.mariayasmim.estetica.entity.Treatment;
import com.mariayasmim.estetica.repository.AppointmentRepository;
import com.mariayasmim.estetica.repository.ProfessionalRepository;
import com.mariayasmim.estetica.repository.TreatmentRepository;
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

    @Autowired
    private ProfessionalRepository professionalRepository;

    @Autowired
    private TreatmentRepository treatmentRepository;

    @PostMapping("/public")
    public ResponseEntity<?> createPublicAppointment(@RequestBody Map<String, Object> payload) {
        try {
            String clientName = (String) payload.get("clientName");
            String clientWhatsapp = (String) payload.get("clientWhatsapp");
            String professionalIdStr = (String) payload.get("professionalId");
            String treatmentIdStr = (String) payload.get("treatmentId");
            String scheduledAtStr = (String) payload.get("scheduledAt");

            Appointment appointment = new Appointment();
            appointment.setClientName(clientName);
            appointment.setClientWhatsapp(clientWhatsapp);
            appointment.setScheduledAt(OffsetDateTime.parse(scheduledAtStr));

            if (professionalIdStr != null && !professionalIdStr.equals("default-pro")) {
                Professional prof = professionalRepository.findById(Long.valueOf(professionalIdStr)).orElse(null);
                appointment.setProfessional(prof);
            }

            if (treatmentIdStr != null) {
                Treatment treatment = treatmentRepository.findById(Long.valueOf(treatmentIdStr)).orElse(null);
                appointment.setTreatment(treatment);
            }

            Appointment saved = appointmentRepository.save(appointment);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
