package com.veterinary.paw.service;

import com.veterinary.paw.domain.*;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateRequestDTO;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateResponseDTO;
import com.veterinary.paw.enums.ApiErrorEnum;
import com.veterinary.paw.exception.PawException;
import com.veterinary.paw.mapper.VeterinaryAppointmentMapper;
import com.veterinary.paw.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VeterinaryAppointmentService {

    private static Logger LOGGER = LoggerFactory.getLogger(VeterinaryAppointmentService.class);

    private final VeterinaryAppointmentRepository veterinaryAppointmentRepository;

    private final PetRepository petRepository;

    private final ShiftRepository shiftRepository;

    private final VeterinaryRepository veterinaryRepository;

    private final VeterinaryServiceRepository veterinaryServiceRepository;

    private final VeterinaryAppointmentMapper veterinaryAppointmentMapper;

    @Transactional
    public VeterinaryAppointmentCreateResponseDTO registerVeterinaryAppointment(VeterinaryAppointmentCreateRequestDTO request) {
        Pet pet = findPet(request.idPet());
        Veterinary veterinary = findVeterinary(request.idVeterinary());
        VeterinaryService service = findVeterinaryService(request.idVeterinaryService());
        Shift shift = findShift(request.idShift());

        // La fecha del turno de la cita debe ser en tiempo futuro
        validateAppointmentDateIsFuture(shift.getDate());

        // La fecha del turno de la cita debe ser en una fecha que el médico trabaja
        List<Shift> veterinaryShifts = getShiftsForVeterinarianOnDate(veterinary.getId(),shift.getDate());

        // El horario inicial y final del turno de la cita estar en el horario inicial y final que el médico trabaja
        Shift coincidentShift = validateShiftTimes(shift, veterinaryShifts);

        // El horario inicial y final del turno no debe tener conflicto de horario con otro turno de cita reservado de ese médico
        List<Shift> reservedShifts = shiftRepository.findReservedShiftsByVeterinaryIdAndDate(veterinary.getId(), shift.getDate());
        validateShiftConflicts(coincidentShift , reservedShifts);

        VeterinaryAppointment appointment = saveVeterinaryAppointment(request, pet, veterinary, service, shift);

        shift.setAvailable(false);
        shiftRepository.save(shift);

        LOGGER.info("Cita registrada correctamente. ID cita: {}, Mascota: {}, Veterinario: {}, Fecha: {}",
                appointment.getId(), pet.getId(), veterinary.getId(), shift.getDate());

        return veterinaryAppointmentMapper.toResponseDTO(appointment);
    }

    private Pet findPet(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Mascota no encontrada. ID: {}", id);
                    return new PawException(ApiErrorEnum.PET_NOT_FOUND);
                });
    }

    private Veterinary findVeterinary(Long id) {
        return veterinaryRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Veterinario no encontrado. ID: {}", id);
                    return new PawException(ApiErrorEnum.VETERINARY_NOT_FOUND);
                });
    }

    private VeterinaryService findVeterinaryService(Long id) {
        return veterinaryServiceRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Servicio veterinario no encontrado. ID: {}", id);
                    return new PawException(ApiErrorEnum.VETERINARY_SERVICE_NOT_FOUND);
                });
    }

    private Shift findShift(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Turno no encontrado. ID: {}", id);
                    return new PawException(ApiErrorEnum.SHIFT_NOT_FOUND);
                });
    }

    // La fecha del turno de la cita debe ser en tiempo futuro
    private void validateAppointmentDateIsFuture(LocalDate date) {
        if (!date.isAfter(LocalDate.now())) {
            LOGGER.error("Fecha de turno inválida. Fecha: {}", date);
            throw new PawException(ApiErrorEnum.INVALID_SHIFT_DATE);
        }
    }

    // La fecha del turno de la cita debe ser en una fecha que el médico trabaja
    private List<Shift> getShiftsForVeterinarianOnDate(Long veterinaryId, LocalDate appointmentDate){
       // obtengo la fecha de la cita, y la busco en los turnos por fecha de los turnos del veterinario
        List<Shift> shifts = shiftRepository.findShiftByVeterinaryIdAndDate(veterinaryId, appointmentDate);
        if (shifts == null || shifts.isEmpty()) {
            LOGGER.error("El veterinario no trabaja en esta Fecha: {}", appointmentDate);
            throw new PawException(ApiErrorEnum.VETERINARY_NOT_AVAILABLE_THIS_DAY);
        }

        return shifts;
    }

    // El horario inicial y final del turno de la cita debe ser en el horario inicial y final que el médico trabaja
    private Shift validateShiftTimes(Shift appointmentShift, List<Shift> veterinaryShifts){
        return veterinaryShifts.stream()
                .filter(s -> !appointmentShift.getStartTime().isBefore(s.getStartTime()) &&
                        !appointmentShift.getEndTime().isAfter(s.getEndTime()))
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error(
                            "No hay turnos del veterinario que coincidan con el horario solicitado: " +
                                    "Fechas del veterinario Veterinario ID: {}, " +
                                    "Fecha que se quiere reservar la cita: {}", veterinaryShifts, appointmentShift);
                    return new PawException(ApiErrorEnum.SHIFT_OUT_OF_WORKING_HOURS);
                });
    }

    // El horario inicial y final del turno no debe tener conflicto de horario con otro turno de cita reservado de ese médico
    private void validateShiftConflicts(Shift newAppointmentShift, List<Shift> existingShifts) {
        boolean hasConflict = existingShifts.stream()
                .anyMatch(s ->
                        (newAppointmentShift.getStartTime().isBefore(s.getEndTime()) &&
                                newAppointmentShift.getEndTime().isAfter(s.getStartTime()))
                );

        if (hasConflict) {
            LOGGER.error(
                    "Conflicto de horario detectado para el veterinario ID {}: " +
                            "El turno solicitado ({}, {}) se superpone con un turno existente.",
                    newAppointmentShift.getVeterinary().getId(),
                    newAppointmentShift.getStartTime(),
                    newAppointmentShift.getEndTime()
            );

            throw new PawException(ApiErrorEnum.VETERINARY_SHIFT_CONFLICT);
        }
    }

    private VeterinaryAppointment saveVeterinaryAppointment(
            VeterinaryAppointmentCreateRequestDTO request,
            Pet pet,
            Veterinary veterinary,
            VeterinaryService service,
            Shift shift
    ) {
        return veterinaryAppointmentRepository.saveVeterinaryAppointment(
                request.status().toString(),
                request.observations(),
                LocalDate.now(),
                pet.getId(),
                veterinary.getId(),
                service.getId(),
                shift.getId()
        );
    }
}
