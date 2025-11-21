package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.Customer;
import com.veterinary.paw.domain.Pet;
import com.veterinary.paw.dto.PetCreateRequestDTO;
import com.veterinary.paw.dto.PetResponseDTO;
import com.veterinary.paw.enums.ApiErrorEnum;
import com.veterinary.paw.exception.PawException;
import com.veterinary.paw.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;

@Component
@RequiredArgsConstructor
public class PetMapper {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    public Pet toEntity(PetCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Customer owner = customerRepository.findById(dto.ownerId())
                .orElseThrow(() -> new PawException(ApiErrorEnum.CUSTOMER_NOT_FOUND));

        int age = Period.between(dto.birthDate(), LocalDate.now()).getYears();

        return Pet.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .gender(dto.gender())
                .specie(dto.specie())
                .birthDate(dto.birthDate())
                .owner(owner)
                .age(age)
                .build();
    }

    public PetResponseDTO toResponseDTO(Pet pet) {
        if (pet == null) {
            return null;
        }

        Customer owner = customerRepository.findById(pet.getOwner().getId())
                .orElseThrow(() -> new PawException(ApiErrorEnum.CUSTOMER_NOT_FOUND));

        return PetResponseDTO.builder()
                .id(pet.getId())
                .firstName(pet.getFirstName())
                .lastName(pet.getLastName())
                .age(pet.getAge())
                .gender(pet.getGender())
                .specie(pet.getSpecie())
                .birthDate(pet.getBirthDate())
                .owner(customerMapper.toResponseDto(owner))
                .build();
    }

    public void updateEntityFromDTO(Pet pet, PetCreateRequestDTO dto) {
        if (pet != null && dto != null) {
            pet.setFirstName(dto.firstName());
            pet.setLastName(dto.lastName());
            pet.setGender(dto.gender());
            pet.setSpecie(dto.specie());
            pet.setBirthDate(dto.birthDate());

            pet.setAge(Period.between(dto.birthDate(), LocalDate.now()).getYears());

            if (!pet.getOwner().getId().equals(dto.ownerId())) {
                Customer newOwner = customerRepository.findById(dto.ownerId())
                        .orElseThrow(() -> new PawException(ApiErrorEnum.CUSTOMER_NOT_FOUND));
                pet.setOwner(newOwner);
            }
        }
    }
}
