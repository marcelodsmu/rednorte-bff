package com.rednorte.bff_rednorte;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class GatewayController {

    private final RestTemplate restTemplate;

    public GatewayController() {
        this.restTemplate = new RestTemplate();
    }

    public GatewayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ===== PACIENTES =====

    @GetMapping("/pacientes")
    public ResponseEntity<?> getPacientes() {
        try {
            return restTemplate.getForEntity("http://localhost:8081/api/pacientes", Object.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Error al conectar con ms-pacientes: " + e.getMessage());
        }
    }

    @GetMapping("/pacientes/{id}")
    public ResponseEntity<?> getPacienteById(@PathVariable Long id) {
        try {
            return restTemplate.getForEntity("http://localhost:8081/api/pacientes/" + id, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(buildErrorBody(e, "Paciente no encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Error al conectar con ms-pacientes: " + e.getMessage());
        }
    }

    @PostMapping("/pacientes")
    public ResponseEntity<?> createPaciente(@RequestBody Object paciente) {
        try {
            return restTemplate.postForEntity("http://localhost:8081/api/pacientes", paciente, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(buildErrorBody(e, "No se pudo crear el paciente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Error al conectar con ms-pacientes: " + e.getMessage());
        }
    }

    @PutMapping("/pacientes/{id}")
    public ResponseEntity<?> updatePaciente(@PathVariable Long id, @RequestBody Object paciente) {
        try {
            restTemplate.put("http://localhost:8081/api/pacientes/" + id, paciente);
            return ResponseEntity.ok("Paciente actualizado");
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(buildErrorBody(e, "Paciente no encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Error al conectar con ms-pacientes: " + e.getMessage());
        }
    }

    @DeleteMapping("/pacientes/{id}")
    public ResponseEntity<?> deletePaciente(@PathVariable Long id) {
        try {
            restTemplate.delete("http://localhost:8081/api/pacientes/" + id);
            return ResponseEntity.ok("Paciente eliminado");
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(buildErrorBody(e, "Paciente no encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Error al conectar con ms-pacientes: " + e.getMessage());
        }
    }

    // ===== CITAS =====

    @GetMapping("/citas")
    public ResponseEntity<?> getCitas() {
        try {
            return restTemplate.getForEntity("http://localhost:8082/api/citas", Object.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Error al conectar con ms-citas: " + e.getMessage());
        }
    }

    @GetMapping("/citas/{id}")
    public ResponseEntity<?> getCitaById(@PathVariable Long id) {
        try {
            return restTemplate.getForEntity("http://localhost:8082/api/citas/" + id, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(buildErrorBody(e, "Cita no encontrada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Error al conectar con ms-citas: " + e.getMessage());
        }
    }

    @PostMapping("/citas")
    public ResponseEntity<?> createCita(@RequestBody Object cita) {
        try {
            return restTemplate.postForEntity("http://localhost:8082/api/citas", cita, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(buildErrorBody(e, "No se pudo crear la cita"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Error al conectar con ms-citas: " + e.getMessage());
        }
    }

    @PutMapping("/citas/{id}")
    public ResponseEntity<?> updateCita(@PathVariable Long id, @RequestBody Object cita) {
        try {
            restTemplate.put("http://localhost:8082/api/citas/" + id, cita);
            return ResponseEntity.ok("Cita actualizada");
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(buildErrorBody(e, "Cita no encontrada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Error al conectar con ms-citas: " + e.getMessage());
        }
    }

    @DeleteMapping("/citas/{id}")
    public ResponseEntity<?> deleteCita(@PathVariable Long id) {
        try {
            restTemplate.delete("http://localhost:8082/api/citas/" + id);
            return ResponseEntity.ok("Cita eliminada");
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode())
                .body(buildErrorBody(e, "Cita no encontrada"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Error al conectar con ms-citas: " + e.getMessage());
        }
    }

    private String buildErrorBody(HttpStatusCodeException exception, String fallbackMessage) {
        String responseBody = exception.getResponseBodyAsString();
        return (responseBody == null || responseBody.isBlank()) ? fallbackMessage : responseBody;
    }
}
