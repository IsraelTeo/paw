package com.veterinary.paw.service;

import com.veterinary.paw.domain.UserEntity;
import com.veterinary.paw.dto.UserCreateRequestDTO;
import com.veterinary.paw.dto.UserResponseDTO;
import com.veterinary.paw.enums.ApiErrorEnum;
import com.veterinary.paw.exception.PawException;
import com.veterinary.paw.mapper.UserMapper;
import com.veterinary.paw.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserEntityService {
    private static Logger LOGGER = LoggerFactory.getLogger(UserEntityService.class);

    private final UserEntityRepository userEntityRepository;

    private final UserMapper userMapper;

    public List<UserResponseDTO> get() {
        return userEntityRepository.findAll().stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getById(Long id) {
        UserEntity user = userEntityRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Usuario no encontrado ID: {}", id);
                    return new PawException(ApiErrorEnum.USER_NOT_FOUND);
                });

        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO register(UserCreateRequestDTO request) {
        UserEntity newUser = userMapper.toEntity(request);
        Optional<UserEntity> userExisting = userEntityRepository.findByEmail(request.email());
        if (userExisting.isPresent()){
            LOGGER.error("Usuario con el email: {} ya existe", request.email());
            throw new PawException(ApiErrorEnum.USER_EMAIL_ALREADY_EXISTS);
        }


        UserEntity savedUser = userEntityRepository.save(newUser);
        LOGGER.info("Usuario registrado exitosamente con email: {}", savedUser.getEmail());
        return userMapper.toResponseDTO(savedUser);
    }

    public UserResponseDTO update(Long id, UserCreateRequestDTO request) {
        UserEntity userToUpdate = userEntityRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Usuario no encontrado para actualizar ID: {}", id);
                    return new PawException(ApiErrorEnum.USER_NOT_FOUND);
                });

        userMapper.updateEntityFromDTO(userToUpdate, request);

        UserEntity updatedUser = userEntityRepository.save(userToUpdate);
        LOGGER.info("Usuario actualizado exitosamente ID: {}", id);
        return userMapper.toResponseDTO(updatedUser);
    }

    public void delete(Long id) {
        if (!userEntityRepository.existsById(id)) {
            LOGGER.error("Usuario no encontrado para eliminar ID: {}", id);
            throw new PawException(ApiErrorEnum.USER_NOT_FOUND);
        }

        userEntityRepository.deleteById(id);
        LOGGER.info("Usuario eliminado exitosamente ID: {}", id);
    }
}
