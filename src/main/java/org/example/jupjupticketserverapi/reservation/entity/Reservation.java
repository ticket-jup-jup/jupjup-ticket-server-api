package org.example.jupjupticketserverapi.reservation.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jupjupticketserverapi.global.entity.BaseEntity;
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
        this.status = ReservationStatus.CONFIRMED;
    }

    public void refund() {
        this.status = ReservationStatus.REFUNDED;
    }

    public void expire() {
        this.status = ReservationStatus.EXPIRED;
    }
}
