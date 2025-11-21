package com.veterinary.paw.controller;

import com.veterinary.paw.dto.VeterinaryCreateRequestDTO;
import com.veterinary.paw.dto.VeterinaryResponseDTO;
import com.veterinary.paw.service.VeterinaryService;
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
@RequestMapping("/veterinaries")
@RequiredArgsConstructor
@Validated
public class VeterinaryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(VeterinaryController.class);

    private final VeterinaryService veterinaryService;

    @GetMapping("/{id}")
    public ResponseEntity<VeterinaryResponseDTO> getVeterinaryById(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método getVeterinaryById() con ID: {}", id);
        VeterinaryResponseDTO response = veterinaryService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<VeterinaryResponseDTO>> getVeterinaries() {
        LOGGER.info("Ingresando al método getVeterinaries() para obtener todos los veterinarios");
        List<VeterinaryResponseDTO> responses = veterinaryService.get();
        if (responses.isEmpty()) {
            LOGGER.warn("No se encontraron veterinarios registrados");
            return ResponseEntity.noContent().build();
        }

        LOGGER.info("Se encontraron {} veterinarios registrados", responses.size());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<VeterinaryResponseDTO> registerVeterinary(
            @RequestBody @Valid VeterinaryCreateRequestDTO request
    ){
        LOGGER.info("➡Ingresando al método registerVeterinary() con datos: firstName={}, lastName={}, dni={}",
                request.firstName(), request.lastName(), request.dni());

        VeterinaryResponseDTO response = veterinaryService.register(request);

        LOGGER.info("Registro exitoso. Nuevo veterinario ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VeterinaryResponseDTO> updateVeterinary(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid VeterinaryCreateRequestDTO request
    ) {
        LOGGER.info("Ingresando al método updateVeterinary() con ID: {} y datos: firstName={}, lastName={}, dni={}",
                id, request.firstName(), request.lastName(), request.dni());

        VeterinaryResponseDTO response = veterinaryService.update(id, request);

        LOGGER.info("Actualización exitosa. Veterinario ID: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeterinary(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método deleteVeterinary() con ID: {}", id);
        veterinaryService.delete(id);

        LOGGER.info("Eliminación exitosa. Veterinario ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
