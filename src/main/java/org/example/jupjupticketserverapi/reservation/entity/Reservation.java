package org.example.jupjupticketserverapi.reservation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jupjupticketserverapi.global.entity.BaseEntity;
import org.example.jupjupticketserverapi.reservation.exception.InvalidReservationStatusException;
import org.example.jupjupticketserverapi.ticket.entity.Ticket;
import org.example.jupjupticketserverapi.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    private LocalDateTime expiresAt;

    public Reservation(
            User user,
            Ticket ticket,
            LocalDateTime expiresAt
    ) {
        this.user = user;
        this.ticket = ticket;
        this.expiresAt = expiresAt;
        this.status = ReservationStatus.PENDING;
    }

    public void confirm() {

        if (this.status != ReservationStatus.PENDING) {
            throw new InvalidReservationStatusException("임시 예약 상태에서만 예약을 확정할 수 있습니다.");
        }

        if (this.expiresAt.isBefore(LocalDateTime.now())) {
            throw new InvalidReservationStatusException("만료된 예약은 확정할 수 없습니다.");
        }

        this.status = ReservationStatus.CONFIRMED;
    }

    public void refund() {
        this.status = ReservationStatus.REFUNDED;
    }

    public void expire() {
        if (this.status != ReservationStatus.PENDING) {
            throw new InvalidReservationStatusException("임시 예약만 만료 처리할 수 있습니다.");
        }

        this.status = ReservationStatus.EXPIRED;
    }
}
