package org.example.jupjupticketserverapi.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jupjupticketserverapi.reservation.entity.Reservation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    private LocalDateTime paidAt;

    private LocalDateTime createdAt;

    public Payment(
            Reservation reservation,
            BigDecimal amount,
            PaymentMethod paymentMethod
    ) {
        LocalDateTime now = LocalDateTime.now();

        this.reservation = reservation;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.COMPLETED;
        this.paidAt = now;
        this.createdAt = now;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }

    public void cancel() {
        this.status = PaymentStatus.CANCELLED;
    }
}
