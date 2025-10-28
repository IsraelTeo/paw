package com.veterinary.paw.repository;

import com.veterinary.paw.domain.VeterinaryAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface VeterinaryAppointmentRepository extends JpaRepository<VeterinaryAppointment, Long> {

    String EXISTS_BY_VETERINARY_AND_SHIFT = "SELECT CASE WHEN COUNT(va) > 0 THEN true ELSE false END FROM VeterinaryAppointment va WHERE va.veterinary.id = :vetId AND va.shift.id = :shiftId";

    @Query(EXISTS_BY_VETERINARY_AND_SHIFT)
    boolean existsByVeterinaryAndShift(@Param("vetId") Long veterinaryId, @Param("shiftId") Long shiftId);

    @Procedure(name = "VeterinaryAppointment.saveVeterinaryAppointment")
    VeterinaryAppointment saveVeterinaryAppointment(
            String status,
            String observations,
            LocalDate registerDate,
            Long idPet,
            Long idVeterinary,
            Long idVeterinaryService,
            Long idShift
    );
}
