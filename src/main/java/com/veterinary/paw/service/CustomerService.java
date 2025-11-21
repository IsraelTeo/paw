package com.veterinary.paw.service;

import com.veterinary.paw.domain.Customer;
import com.veterinary.paw.dto.CustomerCreateRequestDTO;
import com.veterinary.paw.dto.CustomerResponseDTO;
import com.veterinary.paw.enums.ApiErrorEnum;
import com.veterinary.paw.exception.PawException;
import com.veterinary.paw.mapper.CustomerMapper;
import com.veterinary.paw.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static Logger LOGGER = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    public List<CustomerResponseDTO> get() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public CustomerResponseDTO getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow( () -> {
                    LOGGER.error("Cliente no encontrado ID: {}", id);
                    return new PawException(ApiErrorEnum.CUSTOMER_NOT_FOUND);
                });

        return customerMapper.toResponseDto(customer);
    }

    public CustomerResponseDTO register(CustomerCreateRequestDTO request) {
        if (customerRepository.isExistsByEmail(request.email())){
            LOGGER.error("El email: {} ya pertenece a otro cliente.", request.email());
            throw new PawException(ApiErrorEnum.CUSTOMER_EMAIL_ALREADY_EXISTS);
        }

        if (customerRepository.isExistsByDni(request.dni())){
            LOGGER.error("El DNI: {} ya pertenece a otro cliente.", request.dni());
            throw new PawException(ApiErrorEnum.CUSTOMER_DNI_ALREADY_EXISTS);
        }

        if (customerRepository.isExistsByPhoneNumber(request.phoneNumber())){
            LOGGER.error("El número de teléfono: {} ya pertenece a otro cliente.", request.phoneNumber());
            throw new PawException(ApiErrorEnum.CUSTOMER_PHONE_NUMBER_ALREADY_EXISTS);
        }

        Customer newCustomer = customerMapper.toEntity(request);

        Customer savedCustomer = customerRepository.save(newCustomer);

        return customerMapper.toResponseDto(savedCustomer);
    }

    public CustomerResponseDTO update(Long id, CustomerCreateRequestDTO request) {
        Customer customerToUpdate = customerRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Cliente no encontrado para actualizar ID: {}", id);
                    return new PawException(ApiErrorEnum.CUSTOMER_NOT_FOUND);
                });

        if (!customerToUpdate.getEmail().equals(request.email())) {
            if (customerRepository.isExistsByEmail(request.email())) {
                LOGGER.error("El email: {} ya pertenece a otro cliente.", request.email());
                throw new PawException(ApiErrorEnum.CUSTOMER_EMAIL_ALREADY_EXISTS);
            }
        }

        if (!customerToUpdate.getDni().equals(request.dni())) {
            if (customerRepository.isExistsByDni(request.dni())) {
                LOGGER.error("El DNI: {} ya pertenece a otro cliente.", request.dni());
                throw new PawException(ApiErrorEnum.CUSTOMER_DNI_ALREADY_EXISTS);
            }
        }

        if (!customerToUpdate.getPhoneNumber().equals(request.phoneNumber())) {
            if (customerRepository.isExistsByPhoneNumber(request.phoneNumber())) {
                LOGGER.error("El número de teléfono: {} ya pertenece a otro cliente.", request.phoneNumber());
                throw new PawException(ApiErrorEnum.CUSTOMER_PHONE_NUMBER_ALREADY_EXISTS);
            }
        }

        customerMapper.updateEntityFromDto(customerToUpdate, request);

        Customer updatedCustomer = customerRepository.save(customerToUpdate);

        return customerMapper.toResponseDto(updatedCustomer);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new PawException(ApiErrorEnum.CUSTOMER_NOT_FOUND);
        }

        customerRepository.deleteById(id);
    }
}
