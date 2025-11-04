package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.*;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateRequestDTO;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateResponseDTO;
import com.veterinary.paw.dto.VeterinaryAppointmentResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VeterinaryAppointmentMapper {

    private final PetMapper petMapper;

    private final VeterinaryMapper veterinaryMapper;

    private final VeterinaryServiceMapper veterinaryServiceMapper;

    private ShiftMapper shiftMapper;

    public VeterinaryAppointmentCreateResponseDTO toCreateResponseDTO(VeterinaryAppointment appointment) {
        return VeterinaryAppointmentCreateResponseDTO.builder()
                .id(appointment.getId())
                .registerDate(appointment.getRegisterDate())
                .status(appointment.getStatus())
                .observations(appointment.getObservations())
                .idPet(appointment.getPet().getId())
                .idVeterinary(appointment.getVeterinary().getId())
                .idVeterinaryService(appointment.getVeterinaryService().getId())
                .idShift(appointment.getShift().getId())
                .build();
    }

    public VeterinaryAppointment toEntity(
            VeterinaryAppointmentCreateRequestDTO request,
            Pet pet,
            Veterinary veterinary,
            VeterinaryService veterinaryService,
            Shift shift
    ) {
        return VeterinaryAppointment.builder()
                .veterinaryService(veterinaryService)
                .shift(shift)
                .pet(pet)
                .veterinary(veterinary)
                .observations(request.observations())
                .status(request.status())
                .build();
    }

    public VeterinaryAppointmentResponseDTO toResponseDTO(VeterinaryAppointment appointment) {
        return VeterinaryAppointmentResponseDTO.builder()
                .id(appointment.getId())
                .registerDate(appointment.getRegisterDate())
                .status(appointment.getStatus())
                .observations(appointment.getObservations())
                .petResponseDTO(petMapper.toResponseDTO(appointment.getPet()))
                .veterinaryResponseDTO(veterinaryMapper.toResponseDTO(appointment.getVeterinary()))
                .veterinaryServiceResponseDTO(veterinaryServiceMapper.toResponseDTO(appointment.getVeterinaryService()))
                .shiftResponseDTO(shiftMapper.toResponseDTO(appointment.getShift()))
                .build();
    }
}
