package org.example.jupjupticketserverapi.reservation.repository;

import org.example.jupjupticketserverapi.reservation.entity.Reservation;
import org.example.jupjupticketserverapi.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByTicketIdAndStatus(
            Long ticketId,
            ReservationStatus status
    );

    boolean existsByTicketIdAndStatusAndExpiresAtAfter(
            Long ticketId,
            ReservationStatus status,
            LocalDateTime now
    );
}
