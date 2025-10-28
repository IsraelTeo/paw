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
@EqualsAndHashCode
@Table(name = "pet")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 100, nullable = false)
    private String first_name;

    @Column(name = "last_name", length = 100, nullable = false)
    private String last_name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender", length = 10, nullable = false)
    private String gender;

    @Column(name = "specie", length = 50, nullable = false)
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

