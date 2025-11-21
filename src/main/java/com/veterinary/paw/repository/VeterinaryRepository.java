package com.veterinary.paw.repository;

import com.veterinary.paw.domain.UserEntity;
import com.veterinary.paw.domain.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VeterinaryRepository extends JpaRepository<Veterinary, Long> {

    Boolean isExistsByEmail(String email);

    Boolean isExistsByPhoneNumber(String phoneNumber);

    Boolean isExistsByDni(String dni);

}
