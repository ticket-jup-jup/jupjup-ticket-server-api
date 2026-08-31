package org.example.jupjupticketserverapi.seat.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1)
    private String section;

    @Column(nullable = false, length = 2)
    private String rowNumber;

    @Column(nullable = false, length = 2)
    private Integer seatNumber;
}
