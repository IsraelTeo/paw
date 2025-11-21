package com.veterinary.paw.mapper;

import com.veterinary.paw.domain.UserEntity;
import com.veterinary.paw.dto.UserCreateRequestDTO;
import com.veterinary.paw.dto.UserResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserEntity toEntity(UserCreateRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        String encodedPassword = dto.password();

        return UserEntity.builder()
                .email(dto.email())
                .password(encodedPassword)
                .build();
    }

    public UserResponseDTO toResponseDTO(UserEntity user) {
        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }

    public void updateEntityFromDTO(UserEntity user, UserCreateRequestDTO dto) {
        if (user != null && dto != null) {
            user.setEmail(dto.email());

            String encodedPassword = dto.password();
            user.setPassword(encodedPassword);
        }
    }
}
