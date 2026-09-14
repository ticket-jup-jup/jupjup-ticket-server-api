package org.example.jupjupticketserverapi.reservation.repository;

import jakarta.validation.constraints.NotBlank;
import org.example.jupjupticketserverapi.reservation.entity.Reservation;
import org.example.jupjupticketserverapi.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    List<Reservation> findAllByStatusAndExpiresAtLessThanEqual(ReservationStatus reservationStatus, LocalDateTime now);

    Optional<Reservation> findByTicketIdAndStatusIn(@NotBlank(message = "티켓 ID를 입력해주세요.") Long ticketId, List<ReservationStatus> statuses);
}
