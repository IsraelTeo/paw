package com.veterinary.paw.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"appointments"})
@Builder
@EqualsAndHashCode(exclude = {"appointments"})
@Table(name = "shift")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shift_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "available", nullable = false)
    private Boolean available = true;

    @ManyToOne(
            targetEntity = Veterinary.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "id_veterinary",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_veterinary_shift")
    )
    private Veterinary veterinary;

    @OneToMany(
            mappedBy = "shift",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<VeterinaryAppointment> appointments;

}