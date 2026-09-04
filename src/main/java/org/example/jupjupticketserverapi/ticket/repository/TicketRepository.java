package org.example.jupjupticketserverapi.ticket.repository;

import jakarta.persistence.LockModeType;
import org.example.jupjupticketserverapi.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    @Query("""
                SELECT
                    t.id,
                    program.id,
                    p.id,
                    s.section,
                    CONCAT(s.seatRow, '-', s.seatNumber),
                    t.price,
                    r
                FROM Ticket t
                JOIN t.performance p
                JOIN p.program program
                JOIN t.seat s
                LEFT JOIN Reservation r
                    ON r.ticket = t
                    AND (
                        r.status = org.example.jupjupticketserverapi.reservation.entity.ReservationStatus.CONFIRMED
                        OR (
                            r.status = org.example.jupjupticketserverapi.reservation.entity.ReservationStatus.PENDING
                            AND r.expiresAt > CURRENT_TIMESTAMP
                        )
                    )
                WHERE (:programId IS NULL OR program.id = :programId)
                  AND (:performanceId IS NULL OR p.id = :performanceId)
                ORDER BY
                    program.id ASC,
                    p.id ASC,
                    s.section ASC,
                    s.seatRow ASC,
                    s.seatNumber ASC
            """)
    List<Object[]> findTicketList(
            @Param("programId") Long programId,
            @Param("performanceId") Long performanceId
    );

    @Query("""
            SELECT
                t,
                p,
                s,
                r
            FROM Ticket t
            JOIN t.performance p
            JOIN t.seat s
            LEFT JOIN Reservation r
                ON r.ticket = t
                AND (
                    r.status =
                        org.example.jupjupticketserverapi.reservation.entity.ReservationStatus.CONFIRMED
                    OR (
                        r.status =
                            org.example.jupjupticketserverapi.reservation.entity.ReservationStatus.PENDING
                        AND r.expiresAt > CURRENT_TIMESTAMP
                    )
                )
            WHERE (:performanceId IS NULL OR p.id = :performanceId)
            ORDER BY
                p.id ASC,
                s.section ASC,
                s.seatRow ASC,
                s.seatNumber ASC
            """)
    List<Object[]> findInternalTicketList(
            @Param("performanceId") Long performanceId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM Ticket t WHERE t.id = :ticketId")
    Optional<Ticket> findByIdForUpdate(@Param("ticketId") Long ticketId);
}
