package com.veterinary.paw.repository;

import com.veterinary.paw.domain.VeterinaryAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface VeterinaryAppointmentRepository extends JpaRepository<VeterinaryAppointment, Long> {

    @Procedure(name = "VeterinaryAppointment.saveVeterinaryAppointment")
    VeterinaryAppointment saveVeterinaryAppointment(
            @Param("p_status") String status,
            @Param("p_observations") String observations,
            @Param("p_register_date") LocalDate registerDate,
            @Param("p_id_pet") Long idPet,
            @Param("p_id_veterinary") Long idVeterinary,
            @Param("p_id_veterinary_service") Long idVeterinaryService,
            @Param("p_id_shift") Long idShift
    );


    /*

        String EXISTS_BY_VETERINARY_ID_AND_SHIFT_ID =
            "SELECT CASE WHEN COUNT(va) > 0 THEN true ELSE false END FROM VeterinaryAppointment va WHERE va.veterinary.id = :vetId AND va.shift.id = :shiftId";

        @Query(EXISTS_BY_VETERINARY_ID_AND_SHIFT_ID)
    boolean  existsByVeterinaryIdAndShiftId(@Param("vetId") Long veterinaryId, @Param("shiftId") Long shiftId);

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
    */

    /*
    @Query(
            value = "SELECT insert_veterinary_appointment(:p_status, :p_observations, :p_register_date, :p_id_pet, :p_id_veterinary, :p_id_veterinary_service, :p_id_shift)",
            nativeQuery = true
    )
     */

}
