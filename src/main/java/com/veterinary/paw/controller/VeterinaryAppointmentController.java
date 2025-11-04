package com.veterinary.paw.controller;

import com.veterinary.paw.dto.VeterinaryAppointmentCreateRequestDTO;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateResponseDTO;
import com.veterinary.paw.dto.VeterinaryAppointmentResponseDTO;
import com.veterinary.paw.service.VeterinaryAppointmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class VeterinaryAppointmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VeterinaryAppointmentController.class);

    private final VeterinaryAppointmentService veterinaryAppointmentService;

    @GetMapping("/{id}")
    public ResponseEntity<VeterinaryAppointmentResponseDTO> getAppointmentById(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método getAppointmentById() con ID: {}", id);
        VeterinaryAppointmentResponseDTO response = veterinaryAppointmentService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VeterinaryAppointmentResponseDTO>> getAppointments() {
        LOGGER.info("Ingresando al método getAppointments() para obtener todas las citas veterinarias");
        List<VeterinaryAppointmentResponseDTO> responses = veterinaryAppointmentService.get();
        if (responses.isEmpty()) {
            LOGGER.warn("No se encontraron citas registradas");
            return ResponseEntity.noContent().build();
        }

        LOGGER.info("Se encontraron {} citas registradas", responses.size());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<VeterinaryAppointmentCreateResponseDTO> registerVeterinaryAppointment(
            @RequestBody @Valid VeterinaryAppointmentCreateRequestDTO request
    ){
        LOGGER.info("➡Ingresando al método registerVeterinaryAppointment() con datos: petId={}, veterinaryId={}, serviceId={}, shiftId={}",
                request.idPet(), request.idVeterinary(), request.idVeterinaryService(), request.idShift());
        VeterinaryAppointmentCreateResponseDTO response = veterinaryAppointmentService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinaryAppointmentCreateResponseDTO> updateAppointment(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid VeterinaryAppointmentCreateRequestDTO request
    ) {
        LOGGER.info("Ingresando al método updateAppointment() con ID: {} y datos: petId={}, veterinaryId={}, serviceId={}, shiftId={}",
                id, request.idPet(), request.idVeterinary(), request.idVeterinaryService(), request.idShift());
        VeterinaryAppointmentCreateResponseDTO response = veterinaryAppointmentService.update(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        LOGGER.info("Ingresando al método deleteAppointment() con ID: {}", id);
        veterinaryAppointmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
