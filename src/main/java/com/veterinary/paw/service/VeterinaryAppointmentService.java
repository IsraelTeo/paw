package com.veterinary.paw.service;

import com.veterinary.paw.domain.*;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateRequestDTO;
import com.veterinary.paw.dto.VeterinaryAppointmentCreateResponseDTO;
import com.veterinary.paw.enums.ApiErrorEnum;
import com.veterinary.paw.enums.DayAvailableEnum;
import com.veterinary.paw.exception.PawException;
import com.veterinary.paw.mapper.VeterinaryAppointmentMapper;
import com.veterinary.paw.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class VeterinaryAppointmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VeterinaryAppointmentService.class);

    private final VeterinaryAppointmentRepository veterinaryAppointmentRepository;
    private final PetRepository petRepository;
    private final ShiftRepository shiftRepository;
    private final VeterinaryRepository veterinaryRepository;
    private final VeterinaryServiceRepository veterinaryServiceRepository;
    private final VeterinaryAppointmentMapper veterinaryAppointmentMapper;

    @Transactional
    public VeterinaryAppointmentCreateResponseDTO registerVeterinaryAppointment(
            VeterinaryAppointmentCreateRequestDTO request) {

        // 1️⃣ Buscar entidades
        Pet pet = findPet(request.idPet());
        Veterinary veterinary = findVeterinary(request.idVeterinary());
        VeterinaryService service = findVeterinaryService(request.idVeterinaryService());
        Shift shift = findShift(request.idShift());

        // 2️⃣ Validaciones de turno y horario
        validateShiftDate(shift);
        validateShiftAvailability(shift);
        validateVeterinaryDay(veterinary, shift);
        validateShiftTime(veterinary, shift);
        validateShiftOwnership(veterinary, shift);
        validateShiftNotBooked(veterinary, shift);

        // 3️⃣ Guardar cita
        VeterinaryAppointment appointment = saveAppointment(request, pet, veterinary, service, shift);

        // 4️⃣ Marcar turno como no disponible
        shift.setAvailable(false);
        shiftRepository.save(shift);

        LOGGER.info("✅ Cita registrada correctamente. ID cita: {}, Mascota: {}, Veterinario: {}, Fecha: {}",
                appointment.getId(), pet.getId(), veterinary.getId(), shift.getDate());

        return veterinaryAppointmentMapper.toResponseDTO(appointment);
    }

    // ======================
    // MÉTODOS PRIVADOS
    // ======================

    private Pet findPet(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("❌ Mascota no encontrada. ID: {}", id);
                    return new PawException(ApiErrorEnum.PET_NOT_FOUND);
                });
    }

    private Veterinary findVeterinary(Long id) {
        return veterinaryRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("❌ Veterinario no encontrado. ID: {}", id);
                    return new PawException(ApiErrorEnum.VETERINARY_NOT_FOUND);
                });
    }

    private VeterinaryService findVeterinaryService(Long id) {
        return veterinaryServiceRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("❌ Servicio veterinario no encontrado. ID: {}", id);
                    return new PawException(ApiErrorEnum.VETERINARY_SERVICE_NOT_FOUND);
                });
    }

    private Shift findShift(Long id) {
        return shiftRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("❌ Turno no encontrado. ID: {}", id);
                    return new PawException(ApiErrorEnum.SHIFT_NOT_FOUND);
                });
    }

    private void validateShiftDate(Shift shift) {
        if (!shift.getDate().isAfter(LocalDate.now())) {
            LOGGER.error("❌ Fecha de turno inválida. Fecha: {}", shift.getDate());
            throw new PawException(ApiErrorEnum.INVALID_SHIFT_DATE);
        }
    }

    private void validateShiftAvailability(Shift shift) {
        if (!Boolean.TRUE.equals(shift.getAvailable())) {
            LOGGER.error("❌ El turno no está disponible. ID turno: {}", shift.getId());
            throw new PawException(ApiErrorEnum.SHIFT_NOT_AVAILABLE);
        }
    }

    private void validateVeterinaryDay(Veterinary veterinary, Shift shift) {
        DayAvailableEnum day = mapDay(shift.getDate().getDayOfWeek());
        if (!veterinary.getAvailableDays().contains(day)) {
            LOGGER.error("❌ Veterinario no trabaja el día del turno. Día turno: {}, Disponibles: {}",
                    day, veterinary.getAvailableDays());
            throw new PawException(ApiErrorEnum.VETERINARY_NOT_AVAILABLE_THIS_DAY);
        }
    }

    private void validateShiftTime(Veterinary veterinary, Shift shift) {
        List<Shift> shifts = shiftRepository.findShiftByVeterinaryIdAndDate(veterinary.getId(), shift.getDate());
        shifts.stream()
                .filter(s -> !shift.getStartTime().isBefore(s.getStartTime())
                        && !shift.getEndTime().isAfter(s.getEndTime()))
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("❌ No hay turnos del veterinario que coincidan con el horario solicitado. Veterinario ID: {}, Fecha: {}",
                            veterinary.getId(), shift.getDate());
                    return new PawException(ApiErrorEnum.SHIFT_OUT_OF_WORKING_HOURS);
                });
    }

    private void validateShiftOwnership(Veterinary veterinary, Shift shift) {
        if (!shift.getVeterinary().getId().equals(veterinary.getId())) {
            LOGGER.error("❌ El turno (ID: {}) no pertenece al veterinario (ID: {}).", shift.getId(), veterinary.getId());
            throw new PawException(ApiErrorEnum.SHIFT_DOES_NOT_BELONG_TO_VETERINARY);
        }
    }

    private void validateShiftNotBooked(Veterinary veterinary, Shift shift) {
        if (veterinaryAppointmentRepository.existsByVeterinaryAndShift(veterinary.getId(), shift.getId())) {
            LOGGER.error("❌ El turno ya está reservado. Veterinario ID: {}, Turno ID: {}", veterinary.getId(), shift.getId());
            throw new PawException(ApiErrorEnum.SHIFT_ALREADY_BOOKED);
        }
    }

    private VeterinaryAppointment saveAppointment(VeterinaryAppointmentCreateRequestDTO request,
                                                  Pet pet,
                                                  Veterinary veterinary,
                                                  VeterinaryService service,
                                                  Shift shift) {
        return veterinaryAppointmentRepository.saveVeterinaryAppointment(
                request.status().name(),
                request.observations(),
                shift.getDate(),
                pet.getId(),
                veterinary.getId(),
                service.getId(),
                shift.getId()
        );
    }

    // ======================
    // MAPEO DE DÍAS
    // ======================
    private DayAvailableEnum mapDay(DayOfWeek day) {
        return DayAvailableEnum.valueOf(day.name());
    }
}
