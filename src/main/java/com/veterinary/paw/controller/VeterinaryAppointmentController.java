package com.veterinary.paw.controller;

import com.veterinary.paw.dto.VeterinaryAppointmentCreateRequestDTO;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateResponseDTO;
import com.veterinary.paw.service.VeterinaryAppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class VeterinaryAppointmentController {

    private final VeterinaryAppointmentService veterinaryAppointmentService;

    @PostMapping
    public ResponseEntity<VeterinaryAppointmentCreateResponseDTO> registerVeterinaryAppointment(
            @RequestBody @Valid VeterinaryAppointmentCreateRequestDTO request
    ){
        VeterinaryAppointmentCreateResponseDTO response = veterinaryAppointmentService.registerVeterinaryAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
