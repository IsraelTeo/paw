package com.veterinary.paw.controller;

import com.veterinary.paw.dto.CustomerCreateRequestDTO;
import com.veterinary.paw.dto.CustomerResponseDTO;
import com.veterinary.paw.service.CustomerService;
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
@RequestMapping("/customers")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método getCustomerById() con ID: {}", id);
        CustomerResponseDTO response = customerService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getCustomers() {
        LOGGER.info("Ingresando al método getCustomers() para obtener todos los clientes");
        List<CustomerResponseDTO> responses = customerService.get();
        if (responses.isEmpty()) {
            LOGGER.warn("No se encontraron clientes registrados");
            return ResponseEntity.noContent().build();
        }

        LOGGER.info("Se encontraron {} clientes registrados", responses.size());
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> registerCustomer(
            @RequestBody @Valid CustomerCreateRequestDTO request
    ){
        LOGGER.info("➡Ingresando al método registerCustomer() con datos: firstName={}, lastName={}, dni={}",
                request.firstName(), request.lastName(), request.dni());

        CustomerResponseDTO response = customerService.register(request);

        LOGGER.info("Registro exitoso. Nuevo cliente ID: {}", response.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid CustomerCreateRequestDTO request
    ) {
        LOGGER.info("Ingresando al método updateCustomer() con ID: {} y datos: firstName={}, lastName={}, dni={}",
                id, request.firstName(), request.lastName(), request.dni());

        CustomerResponseDTO response = customerService.update(id, request);

        LOGGER.info("Actualización exitosa. Cliente ID: {}", response.id());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable @Min(1) Long id) {
        LOGGER.info("Ingresando al método deleteCustomer() con ID: {}", id);
        customerService.delete(id);

        LOGGER.info("Eliminación exitosa. Cliente ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
