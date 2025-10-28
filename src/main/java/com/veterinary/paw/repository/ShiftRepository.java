package com.veterinary.paw.repository;

import com.veterinary.paw.domain.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    String FIND_SHIFT_BY_VETERINARY_AND_DATE = "SELECT s FROM Shift s WHERE s.veterinary.id = :veterinaryId AND s.date = :date";

    @Query(FIND_SHIFT_BY_VETERINARY_AND_DATE)
    List<Shift> findShiftByVeterinaryIdAndDate(@Param("veterinaryId") Long veterinaryId, @Param("date") LocalDate date);

}
