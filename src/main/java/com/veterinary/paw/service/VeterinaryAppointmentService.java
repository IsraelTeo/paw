package com.veterinary.paw.service;

import com.veterinary.paw.domain.*;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateRequestDTO;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateResponseDTO;
import com.veterinary.paw.dto.VeterinaryAppointmentResponseDTO;
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

    @Transactional(readOnly = true)
    public List<VeterinaryAppointmentResponseDTO> get(){
        List<VeterinaryAppointment> appointments = veterinaryAppointmentRepository.findAll();
        return appointments.stream()
                .map(veterinaryAppointmentMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public VeterinaryAppointmentResponseDTO getById(Long id){
        VeterinaryAppointment appointment = veterinaryAppointmentRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Cita no encontrada. ID: {}", id);
                    return new PawException(ApiErrorEnum.VETERINARY_APPOINTMENT_NOT_FOUND);
                    });

        return veterinaryAppointmentMapper.toResponseDTO(appointment);
    }

    @Transactional
    public VeterinaryAppointmentCreateResponseDTO register(VeterinaryAppointmentCreateRequestDTO request) {
        Pet pet = findPet(request.idPet());
        Veterinary veterinary = findVeterinary(request.idVeterinary());
        VeterinaryService service = findVeterinaryService(request.idVeterinaryService());
        Shift shift = findShift(request.idShift());

        validateAppointmentDateIsFuture(shift.getDate());

        List<Shift> veterinaryShifts = getShiftsForVeterinarianOnDate(veterinary.getId(),shift.getDate());
        Shift coincidentShift = validateShiftTimes(shift, veterinaryShifts);

        List<Shift> reservedShifts = shiftRepository.findReservedShiftsByVeterinaryIdAndDate(veterinary.getId(), shift.getDate());
        validateShiftConflicts(coincidentShift , reservedShifts);

        VeterinaryAppointment appointment = VeterinaryAppointment.builder()
                .status(request.status())
                .observations(request.observations())
                .registerDate(LocalDate.now())
                .pet(pet)
                .veterinary(veterinary)
                .veterinaryService(service)
                .shift(shift)
                .build();

        veterinaryAppointmentRepository.save(appointment);

        shift.setAvailable(false);
        shiftRepository.save(shift);

        LOGGER.info("Cita registrada correctamente. ID cita: {}, Mascota: {}, Veterinario: {}, Fecha: {}",
                appointment.getId(), pet.getId(), veterinary.getId(), shift.getDate());

        return veterinaryAppointmentMapper.toCreateResponseDTO(appointment);
    }

    @Transactional
    public VeterinaryAppointmentCreateResponseDTO update(Long id, VeterinaryAppointmentCreateRequestDTO request) {
        VeterinaryAppointment existingAppointment = veterinaryAppointmentRepository.findById(id)
                .orElseThrow(()->{
                    LOGGER.error("Cita veterinaria no encontrada para actualizar. ID: {}", id);
                    return new PawException(ApiErrorEnum.VETERINARY_APPOINTMENT_NOT_FOUND);
                });

        Pet pet = findPet(request.idPet());
        Veterinary veterinary = findVeterinary(request.idVeterinary());
        VeterinaryService service = findVeterinaryService(request.idVeterinaryService());
        Shift newShift = findShift(request.idShift());

        validateAppointmentDateIsFuture(newShift.getDate());

        List<Shift> veterinaryShifts = getShiftsForVeterinarianOnDate(veterinary.getId(), newShift.getDate());

        Shift coincidentShift = validateShiftTimes(newShift, veterinaryShifts);

        List<Shift> reservedShifts = shiftRepository.findReservedShiftsByVeterinaryIdAndDate(veterinary.getId(), newShift.getDate());
        validateShiftConflicts(coincidentShift , reservedShifts);

        Shift previousShift = existingAppointment.getShift();
        if (!previousShift.getId().equals(newShift.getId())) {
            previousShift.setAvailable(true);
            shiftRepository.save(previousShift);
        }

        existingAppointment.setStatus(request.status());
        existingAppointment.setObservations(request.observations());
        existingAppointment.setRegisterDate(LocalDate.now());
        existingAppointment.setPet(pet);
        existingAppointment.setVeterinary(veterinary);
        existingAppointment.setVeterinaryService(service);
        existingAppointment.setShift(newShift);

        newShift.setAvailable(false);
        shiftRepository.save(newShift);

        VeterinaryAppointment updatedAppointment = veterinaryAppointmentRepository.save(existingAppointment);

        LOGGER.info("Cita actualizada correctamente. ID cita: {}, Mascota: {}, Veterinario: {}, Fecha: {}",
                updatedAppointment.getId(), pet.getId(), veterinary.getId(), newShift.getDate());


        return veterinaryAppointmentMapper.toCreateResponseDTO(updatedAppointment);
    }

    @Transactional
    public void delete(Long id){
        VeterinaryAppointment existingAppointment = veterinaryAppointmentRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Cita veterinaria no encontrada para eliminar. ID: {}", id);
                    return new PawException(ApiErrorEnum.VETERINARY_APPOINTMENT_NOT_FOUND);
                });

        if (existingAppointment.getShift()!= null) {
            existingAppointment.getShift().setAvailable(true);
            shiftRepository.save(existingAppointment.getShift());
        }

        veterinaryAppointmentRepository.delete(existingAppointment);
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

    private void validateAppointmentDateIsFuture(LocalDate date) {
        if (!date.isAfter(LocalDate.now())) {
            LOGGER.error("Fecha de turno inválida. Fecha: {}", date);
            throw new PawException(ApiErrorEnum.INVALID_SHIFT_DATE);
        }
    }

    private List<Shift> getShiftsForVeterinarianOnDate(Long veterinaryId, LocalDate appointmentDate){
        List<Shift> shifts = shiftRepository.findShiftByVeterinaryIdAndDate(veterinaryId, appointmentDate);
        if (shifts == null || shifts.isEmpty()) {
            LOGGER.error("El veterinario no trabaja en esta Fecha: {}", appointmentDate);
            throw new PawException(ApiErrorEnum.VETERINARY_NOT_AVAILABLE_THIS_DAY);
        }

        return shifts;
    }

    private Shift validateShiftTimes(Shift appointmentShift, List<Shift> veterinaryShifts){
        return veterinaryShifts.stream()
                .filter(s ->
                        !appointmentShift.getStartTime().isBefore(s.getStartTime()) &&
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

    private void validateShiftConflicts(Shift newAppointmentShift, List<Shift> existingShifts) {
        boolean hasConflict = existingShifts.stream()
                .anyMatch(s -> (
                                newAppointmentShift.getStartTime().isBefore(s.getEndTime()) &&
                                newAppointmentShift.getEndTime().isAfter(s.getStartTime())
                        )
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
}
