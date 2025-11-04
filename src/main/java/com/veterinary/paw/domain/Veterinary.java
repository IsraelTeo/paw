package com.veterinary.paw.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"appointments", "shifts"})
@Builder
@EqualsAndHashCode(exclude = {"appointments", "shifts"})
@Table(name = "veterinary")
public class Veterinary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "speciality", length = 100, nullable = false)
    private String speciality;

    @Column(name = "phone_number", length = 20, nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "dni", length = 20, nullable = false, unique = true)
    private String dni;

    @OneToMany(
            mappedBy = "veterinary",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<Shift> shifts;

    @OneToMany(
            mappedBy = "veterinary",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    private List<VeterinaryAppointment> appointments;


    /*
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = DayAvailableEnum.class)
    @CollectionTable(
            name = "veterinary_available_days",
            joinColumns = @JoinColumn(name = "veterinary_id")
    )
    @Column(name = "available_days")
    private List<DayOfWeek> availableDays;

     */

}