package com.veterinary.paw.service;

import com.veterinary.paw.domain.Shift;
import com.veterinary.paw.dto.ShiftCreateRequestDTO;
import com.veterinary.paw.dto.ShiftResponseDTO;
import com.veterinary.paw.enums.ApiErrorEnum;
import com.veterinary.paw.exception.PawException;
import com.veterinary.paw.mapper.ShiftMapper;
import com.veterinary.paw.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftService {

    private static Logger LOGGER = LoggerFactory.getLogger(ShiftService.class);

    private final ShiftRepository shiftRepository;

    private final ShiftMapper shiftMapper;

    public List<ShiftResponseDTO> get() {
        return shiftRepository.findAll().stream()
                .map(shiftMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ShiftResponseDTO getById(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Turno no encontrado ID: {}", id);
                    return new PawException(ApiErrorEnum.SHIFT_NOT_FOUND);
                });

        return shiftMapper.toResponseDTO(shift);
    }

    public ShiftResponseDTO register(ShiftCreateRequestDTO request) {
        Shift newShift = shiftMapper.toEntity(request);
        Shift savedShift = shiftRepository.save(newShift);
        return shiftMapper.toResponseDTO(savedShift);
    }

    public ShiftResponseDTO update(Long id, ShiftCreateRequestDTO request) {
        Shift shiftToUpdate = shiftRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Turno no encontrado para actualizar ID: {}", id);
                    return new PawException(ApiErrorEnum.SHIFT_NOT_FOUND);
                });

        shiftMapper.updateEntityFromDto(shiftToUpdate, request);

        Shift updatedShift = shiftRepository.save(shiftToUpdate);

        return shiftMapper.toResponseDTO(updatedShift);
    }

    public void delete(Long id) {
        if (!shiftRepository.existsById(id)) {
            LOGGER.error("Turno no encontrado para eliminar ID: {}", id);
            throw new PawException(ApiErrorEnum.SHIFT_NOT_FOUND);
        }

        shiftRepository.deleteById(id);
        LOGGER.info("Turno eliminado exitosamente ID: {}", id);
    }
}
