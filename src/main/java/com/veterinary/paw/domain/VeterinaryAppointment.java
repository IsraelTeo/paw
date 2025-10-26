package com.veterinary.paw.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table(name = "cita_medica")
public class MedicalAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_registro", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDate registrationDate;

    @Column(name = "estado", length = 50)
    private String status = "PENDIENTE";

    @Column(name = "observaciones")
    private String observations;

    @ManyToOne(
            targetEntity = Pet.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "id_mascota",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_mascota_cita")
    )
    private Pet pet;

    @ManyToOne(
            targetEntity = Veterinary.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "id_medico",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_medico_cita")
    )
    private Veterinary doctor;

    @ManyToOne(
            targetEntity = VeterinaryService.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "id_servicio",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_servicio_cita")
    )
    private VeterinaryService service;

    @ManyToOne(
            targetEntity = Shift.class,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "id_turno",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_turno_cita")
    )
    private Shift shift;
}
