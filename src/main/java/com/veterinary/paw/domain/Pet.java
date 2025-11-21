package com.veterinary.paw.domain;

import com.veterinary.paw.enums.PetGenderEnum;
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
@EqualsAndHashCode
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    private Integer age;

    @Column(length = 10, nullable = false)
    @Enumerated(EnumType.STRING)
    private PetGenderEnum gender;

    private String specie;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToOne(
            targetEntity = Customer.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL
    )
    @JoinColumn(
            name = "id_customer",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_customer_pet")
    )
    private Customer owner;
}

