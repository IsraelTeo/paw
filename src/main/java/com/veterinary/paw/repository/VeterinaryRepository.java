package com.veterinary.paw.repository;

import com.veterinary.paw.domain.Veterinary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinaryRepository extends JpaRepository<Veterinary, Long> {

}
