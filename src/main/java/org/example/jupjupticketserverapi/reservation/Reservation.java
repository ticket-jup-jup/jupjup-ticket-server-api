package org.example.jupjupticketserverapi.reservation;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jupjupticketserverapi.ticket.entity.Ticket;
import org.example.jupjupticketserverapi.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

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
            ReservationStatus status,
            LocalDateTime expiresAt
    ) {
        this.user = user;
        this.ticket = ticket;
        this.status = status;
        this.expiresAt = expiresAt;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = ReservationStatus.CANCELLED;
    }

    public void expire() {
        this.status = ReservationStatus.EXPIRED;
    }
}
