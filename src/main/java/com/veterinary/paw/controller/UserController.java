package com.veterinary.paw.controller;

import com.veterinary.paw.dto.UserCreateRequestDTO;
import com.veterinary.paw.dto.UserResponseDTO;
import com.veterinary.paw.service.UserEntityService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserEntityService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método getUserById() con ID: {}", id);
        UserResponseDTO response = userService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        LOGGER.info("Ingresando al método getUsers() para obtener todos los usuarios");
        List<UserResponseDTO> responses = userService.get();
        if (responses.isEmpty()) {
            LOGGER.warn("No se encontraron usuarios registrados");
            return ResponseEntity.noContent().build();
        }

        LOGGER.info("Se encontraron {} usuarios registrados", responses.size());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(
            @RequestBody @Valid UserCreateRequestDTO request
    ){
        LOGGER.info("➡Ingresando al método registerUser() con email: {}", request.email());

        UserResponseDTO response = userService.register(request);

        LOGGER.info("Registro exitoso. Nuevo usuario ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid UserCreateRequestDTO request
    ) {
        LOGGER.info("Ingresando al método updateUser() con ID: {} y nuevo email: {}", id, request.email());

        UserResponseDTO response = userService.update(id, request);

        LOGGER.info("Actualización exitosa. Usuario ID: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método deleteUser() con ID: {}", id);
        userService.delete(id);

        LOGGER.info("Eliminación exitosa. Usuario ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
