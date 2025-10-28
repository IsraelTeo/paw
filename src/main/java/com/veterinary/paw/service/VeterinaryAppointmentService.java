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
import java.time.LocalTime;
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
    public VeterinaryAppointmentCreateResponseDTO registerVeterinaryAppointment(VeterinaryAppointmentCreateRequestDTO veterinaryAppointmentRequest){

        Pet pet = petRepository.findById(veterinaryAppointmentRequest.idPet())
                .orElseThrow(() -> {
                    LOGGER.error("❌ Mascota no encontrada. ID: {}", veterinaryAppointmentRequest.idPet());
                    return new PawException(ApiErrorEnum.PET_NOT_FOUND);
                });

        Veterinary veterinary = veterinaryRepository.findById(veterinaryAppointmentRequest.idVeterinary())
                .orElseThrow(() -> {
                    LOGGER.error("❌ Veterinario no encontrado. ID: {}", veterinaryAppointmentRequest.idVeterinary());
                    return new PawException(ApiErrorEnum.VETERINARY_NOT_FOUND);
                });

        VeterinaryService veterinaryService = veterinaryServiceRepository.findById(veterinaryAppointmentRequest.idVeterinaryService())
                .orElseThrow(() -> {
                    LOGGER.error("❌ Servicio veterinario no encontrado. ID: {}", veterinaryAppointmentRequest.idVeterinaryService());
                    return new PawException(ApiErrorEnum.VETERINARY_SERVICE_NOT_FOUND);
                });

        Shift appointmentShift = shiftRepository.findById(veterinaryAppointmentRequest.idShift())
                .orElseThrow(() -> {
                    LOGGER.error("❌ Turno no encontrado. ID: {}", veterinaryAppointmentRequest.idShift());
                    return new PawException(ApiErrorEnum.SHIFT_NOT_FOUND);
                });

        LOGGER.info("📅 Fecha del turno: {}", appointmentShift.getDate());
        LOGGER.info("🕒 Hora inicio: {}, Hora fin: {}", appointmentShift.getStartTime(), appointmentShift.getEndTime());
        LOGGER.info("🧑‍⚕️ Veterinario asignado al turno: {}", appointmentShift.getVeterinary().getId());

        if (!Boolean.TRUE.equals(appointmentShift.getAvailable())) {
            LOGGER.error("❌ El turno no está disponible. ID turno: {}", appointmentShift.getId());
            throw new PawException(ApiErrorEnum.SHIFT_NOT_AVAILABLE);
        }

        if (!appointmentShift.getDate().isAfter(LocalDate.now())) {
            LOGGER.error("❌ La fecha del turno no es válida. Fecha del turno: {}, Fecha actual: {}", appointmentShift.getDate(), LocalDate.now());
            throw new PawException(ApiErrorEnum.INVALID_SHIFT_DATE);
        }

        DayOfWeek dayOfAppointment = appointmentShift.getDate().getDayOfWeek();
        DayAvailableEnum currentDay = mapDay(dayOfAppointment);
        LOGGER.info("📆 Día del turno: {}, Día convertido: {}", dayOfAppointment, currentDay);
        LOGGER.info("🧑‍⚕️ Días disponibles del veterinario: {}", veterinary.getAvailableDays());

        if (!veterinary.getAvailableDays().contains(currentDay)) {
            LOGGER.error("❌ El veterinario no trabaja el día del turno. Día turno: {}, Días disponibles: {}",
                    currentDay, veterinary.getAvailableDays());
            throw new PawException(ApiErrorEnum.VETERINARY_NOT_AVAILABLE_THIS_DAY);
        }

        List<Shift> shifts = shiftRepository.findShiftByVeterinaryIdAndDate(veterinary.getId(), appointmentShift.getDate());
        if (shifts.isEmpty()) {
            LOGGER.error("❌ No se encontraron turnos para el veterinario {} en la fecha {}",
                    veterinary.getId(), appointmentShift.getDate());
            throw new PawException(ApiErrorEnum.SHIFT_NOT_FOUND);
        }

        Shift veterinaryShift = shifts.stream()
                .filter(s ->
                        !appointmentShift.getStartTime().isBefore(s.getStartTime()) &&
                                !appointmentShift.getEndTime().isAfter(s.getEndTime())
                )
                .findFirst()
                .orElseThrow(() -> {
                    LOGGER.error("❌ No hay turnos del veterinario que coincidan con el horario solicitado. Veterinario ID: {}, Fecha: {}, Turnos encontrados: {}",
                            veterinary.getId(), appointmentShift.getDate(), shifts);
                    return new PawException(ApiErrorEnum.SHIFT_OUT_OF_WORKING_HOURS);
                });

        if (!appointmentShift.getVeterinary().getId().equals(veterinary.getId())) {
            LOGGER.error("❌ El turno (ID: {}) no pertenece al veterinario (ID: {}).",
                    appointmentShift.getId(), veterinary.getId());
            throw new PawException(ApiErrorEnum.SHIFT_DOES_NOT_BELONG_TO_VETERINARY);
        }

        boolean hasAnotherAppointment = veterinaryAppointmentRepository.existsByVeterinaryAndShift(veterinary.getId(), appointmentShift.getId());
        if (hasAnotherAppointment) {
            LOGGER.error("❌ El turno ya está reservado. Veterinario ID: {}, Turno ID: {}",
                    veterinary.getId(), appointmentShift.getId());
            throw new PawException(ApiErrorEnum.SHIFT_ALREADY_BOOKED);
        }

        LOGGER.info("💾 Guardando cita veterinaria...");
        VeterinaryAppointment appointment = veterinaryAppointmentRepository.saveVeterinaryAppointment(
                veterinaryAppointmentRequest.status().name(),
                veterinaryAppointmentRequest.observations(),
                appointmentShift.getDate(),
                pet.getId(),
                veterinary.getId(),
                veterinaryService.getId(),
                appointmentShift.getId()
        );

        appointmentShift.setAvailable(false);
        shiftRepository.save(appointmentShift);
        LOGGER.info("✅ Cita registrada correctamente. ID cita: {}, Mascota: {}, Veterinario: {}, Fecha: {}",appointment.getId(), pet.getId(), veterinary.getId(), appointmentShift.getDate());
        LOGGER.info("==== ✅ FIN DE REGISTRO DE CITA ====");
        return veterinaryAppointmentMapper.toResponseDTO(appointment);
    }

    private DayAvailableEnum mapDay(DayOfWeek day) {
        return switch (day) {
            case MONDAY -> DayAvailableEnum.MONDAY;
            case TUESDAY -> DayAvailableEnum.TUESDAY;
            case WEDNESDAY -> DayAvailableEnum.WEDNESDAY;
            case THURSDAY -> DayAvailableEnum.THURSDAY;
            case FRIDAY -> DayAvailableEnum.FRIDAY;
            case SATURDAY -> DayAvailableEnum.SATURDAY;
            case SUNDAY -> DayAvailableEnum.SUNDAY;
            default -> throw new PawException(ApiErrorEnum.VETERINARY_NOT_AVAILABLE_THIS_DAY);
        };
    }
}
