package com.veterinary.paw.repository;

import com.veterinary.paw.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    Boolean isExistsByEmail(String email);
}
