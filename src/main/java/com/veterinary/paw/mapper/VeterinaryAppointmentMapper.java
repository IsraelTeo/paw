package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.*;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateRequestDTO;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class VeterinaryAppointmentMapper {

    public VeterinaryAppointmentCreateResponseDTO toResponseDTO(VeterinaryAppointment appointment) {
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


}
