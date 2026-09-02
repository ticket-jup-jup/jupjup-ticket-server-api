package org.example.jupjupticketserverapi.ticket.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jupjupticketserverapi.global.entity.BaseEntity;
import org.example.jupjupticketserverapi.performance.entity.Performance;
import org.example.jupjupticketserverapi.seat.entity.Seat;

import java.math.BigDecimal;

@Entity
@Table(name = "tickets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id", nullable = false)
    private Performance performance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TicketStatus status;

    public Ticket(Performance performance, Seat seat, BigDecimal price) {
        this.performance = performance;
        this.seat = seat;
        this.price = price;
        this.status = TicketStatus.AVAILABLE;
    }

    public void cancel() {this.status = TicketStatus.CANCELLED;}

    public void reserve() {
        this.status = TicketStatus.RESERVED;
    }

    public void confirm() {
        this.status = TicketStatus.SOLD;
    }
}
