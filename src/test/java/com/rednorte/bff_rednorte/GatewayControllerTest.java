package com.rednorte.bff_rednorte;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

class GatewayControllerTest {

    @Mock
    private RestTemplate restTemplate;

    private GatewayController gatewayController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        gatewayController = new GatewayController(restTemplate);
    }

    @Test
    void getPacientesSuccess() {
        ResponseEntity<Object> expected = ResponseEntity.ok(List.of(Map.of("id", 1L, "nombre", "Ana")));
        when(restTemplate.getForEntity("http://localhost:8081/api/pacientes", Object.class)).thenReturn(expected);

        ResponseEntity<?> result = gatewayController.getPacientes();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void getPacientesError() {
        when(restTemplate.getForEntity("http://localhost:8081/api/pacientes", Object.class))
            .thenThrow(new RuntimeException("down"));

        ResponseEntity<?> result = gatewayController.getPacientes();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode());
        assertTrue(String.valueOf(result.getBody()).contains("Error al conectar con ms-pacientes"));
    }

    @Test
    void getPacienteByIdSuccess() {
        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 5L, "nombre", "Luis"));
        when(restTemplate.getForEntity("http://localhost:8081/api/pacientes/5", Object.class)).thenReturn(expected);

        ResponseEntity<?> result = gatewayController.getPacienteById(5L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void createPacienteSuccess() {
        Map<String, Object> payload = Map.of("nombre", "Paula", "rut", "11-1");
        ResponseEntity<Object> expected = new ResponseEntity<>(Map.of("id", 1L), HttpStatus.CREATED);
        when(restTemplate.postForEntity("http://localhost:8081/api/pacientes", payload, Object.class)).thenReturn(expected);

        ResponseEntity<?> result = gatewayController.createPaciente(payload);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void updatePacienteSuccess() {
        Map<String, Object> payload = Map.of("nombre", "Actualizado", "rut", "22-2");
        doNothing().when(restTemplate).put("http://localhost:8081/api/pacientes/9", payload);

        ResponseEntity<?> result = gatewayController.updatePaciente(9L, payload);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Paciente actualizado", result.getBody());
    }

    @Test
    void deletePacienteSuccess() {
        doNothing().when(restTemplate).delete("http://localhost:8081/api/pacientes/3");

        ResponseEntity<?> result = gatewayController.deletePaciente(3L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Paciente eliminado", result.getBody());
    }

    @Test
    void deletePacienteNotFound() {
        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
            .when(restTemplate).delete("http://localhost:8081/api/pacientes/2");

        ResponseEntity<?> result = gatewayController.deletePaciente(2L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Paciente no encontrado", result.getBody());
    }

    @Test
    void getCitasSuccess() {
        ResponseEntity<Object> expected = ResponseEntity.ok(List.of(Map.of("id", 1L, "especialidad", "Cardiologia")));
        when(restTemplate.getForEntity("http://localhost:8082/api/citas", Object.class)).thenReturn(expected);

        ResponseEntity<?> result = gatewayController.getCitas();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expected.getBody(), result.getBody());
    }

    @Test
    void getCitaByIdSuccess() {
        ResponseEntity<Object> expected = ResponseEntity.ok(Map.of("id", 8L, "especialidad", "Dermatologia"));
        when(restTemplate.getForEntity("http://localhost:8082/api/citas/8", Object.class)).thenReturn(expected);

        ResponseEntity<?> result = gatewayController.getCitaById(8L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void createCitaSuccess() {
        Map<String, Object> payload = Map.of("fecha", "2026-06-22", "especialidad", "Traumatologia", "idPaciente", 1);
        ResponseEntity<Object> expected = new ResponseEntity<>(Map.of("id", 4L), HttpStatus.CREATED);
        when(restTemplate.postForEntity("http://localhost:8082/api/citas", payload, Object.class)).thenReturn(expected);

        ResponseEntity<?> result = gatewayController.createCita(payload);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void updateCitaError() {
        Map<String, Object> payload = Map.of("especialidad", "Neurologia");
        doThrow(new RuntimeException("down")).when(restTemplate).put("http://localhost:8082/api/citas/7", payload);

        ResponseEntity<?> result = gatewayController.updateCita(7L, payload);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode());
        assertTrue(String.valueOf(result.getBody()).contains("Error al conectar con ms-citas"));
    }

    @Test
    void deleteCitaSuccess() {
        doNothing().when(restTemplate).delete("http://localhost:8082/api/citas/2");

        ResponseEntity<?> result = gatewayController.deleteCita(2L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Cita eliminada", result.getBody());
        verify(restTemplate).delete(eq("http://localhost:8082/api/citas/2"));
    }

    @Test
    void createPacienteError() {
        when(restTemplate.postForEntity(eq("http://localhost:8081/api/pacientes"), any(), eq(Object.class)))
            .thenThrow(new RuntimeException("down"));

        ResponseEntity<?> result = gatewayController.createPaciente(Map.of("nombre", "Error"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, result.getStatusCode());
        assertTrue(String.valueOf(result.getBody()).contains("Error al conectar con ms-pacientes"));
    }
}
