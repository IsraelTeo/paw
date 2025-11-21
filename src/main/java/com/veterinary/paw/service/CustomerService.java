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
        Customer newCustomer = customerMapper.toEntity(request);

        Customer savedCustomer = customerRepository.save(newCustomer);

        return customerMapper.toResponseDto(savedCustomer);
    }

    public CustomerResponseDTO update(Long id, CustomerCreateRequestDTO request) {
        Customer customerToUpdate = customerRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Cliente no para actualizar ID: {}", id);
                    return new PawException(ApiErrorEnum.CUSTOMER_NOT_FOUND);
                });

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
