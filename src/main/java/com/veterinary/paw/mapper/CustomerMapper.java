package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.Customer;
import com.veterinary.paw.dto.CustomerCreateRequestDTO;
import com.veterinary.paw.dto.CustomerResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return Customer.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .dni(dto.dni())
                .phoneNumber(dto.phoneNumber())
                .email(dto.email())
                .build();
    }

    public CustomerResponseDTO toResponseDto(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .dni(customer.getDni())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .build();
    }

    public void updateEntityFromDto(Customer customer, CustomerCreateRequestDTO dto) {
        if (customer != null && dto != null) {
            customer.setFirstName(dto.firstName());
            customer.setLastName(dto.lastName());
            customer.setDni(dto.dni());
            customer.setPhoneNumber(dto.phoneNumber());
            customer.setEmail(dto.email());
        }
    }
}
