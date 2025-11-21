package com.veterinary.paw.controller;

import com.veterinary.paw.dto.ShiftCreateRequestDTO;
import com.veterinary.paw.dto.ShiftResponseDTO;
import com.veterinary.paw.service.ShiftService;
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
@RequestMapping("/shifts")
@RequiredArgsConstructor
@Validated
public class ShiftController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiftController.class);

    private final ShiftService shiftService;

    @GetMapping("/{id}")
    public ResponseEntity<ShiftResponseDTO> getShiftById(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método getShiftById() con ID: {}", id);
        ShiftResponseDTO response = shiftService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ShiftResponseDTO>> getShifts() {
        LOGGER.info("Ingresando al método getShifts() para obtener todos los turnos");
        List<ShiftResponseDTO> responses = shiftService.get();
        if (responses.isEmpty()) {
            LOGGER.warn("No se encontraron turnos registrados");
            return ResponseEntity.noContent().build();
        }

        LOGGER.info("Se encontraron {} turnos registrados", responses.size());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<ShiftResponseDTO> registerShift(
            @RequestBody @Valid ShiftCreateRequestDTO request
    ){
        LOGGER.info("➡Ingresando al método registerShift() con datos: date={}, startTime={}, veterinaryId={}",
                request.date(), request.startTime(), request.veterinaryId());

        ShiftResponseDTO response = shiftService.register(request);

        LOGGER.info("Registro exitoso. Nuevo turno ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ShiftResponseDTO> updateShift(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid ShiftCreateRequestDTO request
    ) {
        LOGGER.info("Ingresando al método updateShift() con ID: {} y datos: date={}, startTime={}, veterinaryId={}",
                id, request.date(), request.startTime(), request.veterinaryId());

        ShiftResponseDTO response = shiftService.update(id, request);

        LOGGER.info("Actualización exitosa. Turno ID: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShift(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método deleteShift() con ID: {}", id);
        shiftService.delete(id);

        LOGGER.info("Eliminación exitosa. Turno ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
