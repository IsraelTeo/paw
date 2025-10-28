package com.veterinary.paw.repository;

import com.veterinary.paw.domain.VeterinaryService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VeterinaryServiceRepository extends JpaRepository<VeterinaryService, Long> {
}
