package com.veterinary.paw.controller;

import com.veterinary.paw.dto.PetCreateRequestDTO;
import com.veterinary.paw.dto.PetResponseDTO;
import com.veterinary.paw.service.PetService;
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
@RequestMapping("/pets")
@RequiredArgsConstructor
@Validated
public class PetController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PetController.class);

    private final PetService petService;

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> getPetById(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método getPetById() con ID: {}", id);
        PetResponseDTO response = petService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> getPets() {
        LOGGER.info("Ingresando al método getPets() para obtener todas las mascotas");
        List<PetResponseDTO> responses = petService.get();
        if (responses.isEmpty()) {
            LOGGER.warn("No se encontraron mascotas registradas");
            return ResponseEntity.noContent().build();
        }

        LOGGER.info("Se encontraron {} mascotas registradas", responses.size());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<PetResponseDTO> registerPet(
            @RequestBody @Valid PetCreateRequestDTO request
    ){
        LOGGER.info("➡Ingresando al método registerPet() con datos: firstName={}, specie={}, ownerId={}",
                request.firstName(), request.specie(), request.ownerId());

        PetResponseDTO response = petService.register(request);

        LOGGER.info("Registro exitoso. Nueva mascota ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> updatePet(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid PetCreateRequestDTO request
    ) {
        LOGGER.info("Ingresando al método updatePet() con ID: {} y datos: firstName={}, specie={}, ownerId={}",
                id, request.firstName(), request.specie(), request.ownerId());

        PetResponseDTO response = petService.update(id, request);

        LOGGER.info("Actualización exitosa. Mascota ID: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método deletePet() con ID: {}", id);
        petService.delete(id);

        LOGGER.info("Eliminación exitosa. Mascota ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
