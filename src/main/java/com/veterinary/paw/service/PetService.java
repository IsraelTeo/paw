package com.veterinary.paw.service;


import com.veterinary.paw.domain.Pet;
import com.veterinary.paw.dto.PetCreateRequestDTO;
import com.veterinary.paw.dto.PetResponseDTO;
import com.veterinary.paw.enums.ApiErrorEnum;
import com.veterinary.paw.exception.PawException;
import com.veterinary.paw.mapper.PetMapper;
import com.veterinary.paw.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private static Logger LOGGER = LoggerFactory.getLogger(PetService.class);

    private final PetRepository petRepository;

    private final PetMapper petMapper;

    public List<PetResponseDTO> get() {
        return petRepository.findAll().stream()
                .map(petMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PetResponseDTO getById(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Mascota no encontrada ID: {}", id);
                    return new PawException(ApiErrorEnum.PET_NOT_FOUND);
                });

        return petMapper.toResponseDTO(pet);
    }

    public PetResponseDTO register(PetCreateRequestDTO request) {
        Pet newPet = petMapper.toEntity(request);

        Pet savedPet = petRepository.save(newPet);

        return petMapper.toResponseDTO(savedPet);
    }

    public PetResponseDTO update(Long id, PetCreateRequestDTO request) {
        Pet petToUpdate = petRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Mascota no encontrada para actualizar ID: {}", id);
                    return new PawException(ApiErrorEnum.PET_NOT_FOUND);
                });

        petMapper.updateEntityFromDTO(petToUpdate, request);

        Pet updatedPet = petRepository.save(petToUpdate);

        return petMapper.toResponseDTO(updatedPet);
    }

    public void delete(Long id) {
        if (!petRepository.existsById(id)) {
            LOGGER.error("Mascota no encontrada para eliminar ID: {}", id);
            throw new PawException(ApiErrorEnum.PET_NOT_FOUND);
        }

        petRepository.deleteById(id);
        LOGGER.info("Mascota eliminada exitosamente ID: {}", id);
    }
}
