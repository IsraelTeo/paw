package com.veterinary.paw.repository;

import com.veterinary.paw.domain.VeterinaryAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface VeterinaryAppointmentRepository extends JpaRepository<VeterinaryAppointment, Long> {

    @Query(
            value = "SELECT insert_veterinary_appointment(:p_status, :p_observations, :p_register_date, :p_id_pet, :p_id_veterinary, :p_id_veterinary_service, :p_id_shift)",
            nativeQuery = true
    )
    Long saveVeterinaryAppointment(
            @Param("p_status") String status,
            @Param("p_observations") String observations,
            @Param("p_register_date") java.sql.Timestamp registerDate,
            @Param("p_id_pet") Long idPet,
            @Param("p_id_veterinary") Long idVeterinary,
            @Param("p_id_veterinary_service") Long idVeterinaryService,
            @Param("p_id_shift") Long idShift
    );

    // ✅ Nuevo metodo para validar si ya existe una cita
    boolean existsByVeterinaryIdAndShiftId(Long veterinaryId, Long shiftId);
}

