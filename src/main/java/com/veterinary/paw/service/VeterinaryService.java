package com.veterinary.paw.service;

import com.veterinary.paw.domain.Veterinary;
import com.veterinary.paw.dto.VeterinaryCreateRequestDTO;
import com.veterinary.paw.dto.VeterinaryResponseDTO;
import com.veterinary.paw.enums.ApiErrorEnum;
import com.veterinary.paw.exception.PawException;
import com.veterinary.paw.mapper.VeterinaryMapper;
import com.veterinary.paw.repository.VeterinaryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VeterinaryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VeterinaryService.class);

    private final VeterinaryRepository veterinaryRepository;

    private final VeterinaryMapper veterinaryMapper;

    public List<VeterinaryResponseDTO> get() {
        return veterinaryRepository.findAll().stream()
                .map(veterinaryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public VeterinaryResponseDTO getById(Long id) {
        Veterinary veterinary = veterinaryRepository.findById(id)
                .orElseThrow( () -> {
                    LOGGER.error("Veterinario no encontrado ID: {}", id);
                    return new PawException(ApiErrorEnum.VETERINARY_NOT_FOUND);
                });

        return veterinaryMapper.toResponseDTO(veterinary);
    }

    public VeterinaryResponseDTO register(VeterinaryCreateRequestDTO request) {
        if (veterinaryRepository.isExistsByEmail(request.dni())){
            LOGGER.error("El email: {} ya pertenece a otro veterinario.", request.email());
            throw new PawException(ApiErrorEnum.VETERINARY_EMAIL_ALREADY_EXISTS);
        }

        if (veterinaryRepository.isExistsByDni(request.email())){
            LOGGER.error("El DNI: {} ya pertenece a otro veterinario.", request.dni());
            throw new PawException(ApiErrorEnum.VETERINARY_DNI_ALREADY_EXISTS);
        }

        if (veterinaryRepository.isExistsByPhoneNumber(request.phoneNumber())){
            LOGGER.error("El número de teléfono: {} ya pertenece a otro veterinario.", request.phoneNumber());
            throw new PawException(ApiErrorEnum.VETERINARY_PHONE_NUMBER_ALREADY_EXISTS);
        }

        Veterinary newVeterinary = veterinaryMapper.toEntity(request);

        Veterinary savedVeterinary = veterinaryRepository.save(newVeterinary);

        return veterinaryMapper.toResponseDTO(savedVeterinary);
    }

    public VeterinaryResponseDTO update(Long id, VeterinaryCreateRequestDTO request) {
        Veterinary veterinaryToUpdate = veterinaryRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Veterinario no encontrado para actualizar ID: {}", id);
                    return new PawException(ApiErrorEnum.VETERINARY_NOT_FOUND);
                });

        if (!veterinaryToUpdate.getEmail().equals(request.email())) {
            if (veterinaryRepository.isExistsByEmail(request.email())) {
                LOGGER.error("El email: {} ya pertenece a otro veterinario.", request.email());
                throw new PawException(ApiErrorEnum.VETERINARY_EMAIL_ALREADY_EXISTS);
            }
        }

        if (!veterinaryToUpdate.getDni().equals(request.dni())) {
            if (veterinaryRepository.isExistsByDni(request.dni())) {
                LOGGER.error("El DNI: {} ya pertenece a otro veterinario.", request.dni());
                throw new PawException(ApiErrorEnum.VETERINARY_DNI_ALREADY_EXISTS);
            }
        }

        if (!veterinaryToUpdate.getPhoneNumber().equals(request.phoneNumber())) {
            if (veterinaryRepository.isExistsByPhoneNumber(request.phoneNumber())) {
                LOGGER.error("El número de teléfono: {} ya pertenece a otro veterinario.", request.phoneNumber());
                throw new PawException(ApiErrorEnum.VETERINARY_PHONE_NUMBER_ALREADY_EXISTS);
            }
        }

        veterinaryMapper.updateEntityFromDTO(veterinaryToUpdate, request);

        Veterinary updatedVeterinary = veterinaryRepository.save(veterinaryToUpdate);

        return veterinaryMapper.toResponseDTO(updatedVeterinary);
    }

    public void delete(Long id) {
        if (!veterinaryRepository.existsById(id)) {
            throw new PawException(ApiErrorEnum.VETERINARY_NOT_FOUND);
        }

        veterinaryRepository.deleteById(id);
    }
}
