package org.example.jupjupticketserverapi.seat.repository;

import org.example.jupjupticketserverapi.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
