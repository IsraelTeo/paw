package com.veterinary.paw.repository;

import com.veterinary.paw.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {

    String QUERY_FIND_USER_BY_EMAIL = "SELECT u FROM UserEntity u WHERE u.email = :email";

    @Query(QUERY_FIND_USER_BY_EMAIL)
    Optional<UserEntity> findByEmail(String email);
}
