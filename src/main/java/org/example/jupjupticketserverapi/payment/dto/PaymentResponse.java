package org.example.jupjupticketserverapi.payment.dto;

import lombok.Getter;
import org.example.jupjupticketserverapi.payment.entity.PaymentMethod;
import org.example.jupjupticketserverapi.payment.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentResponse {

    private final Long paymentId;
    private final Long reservationId;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private final PaymentStatus status;
    private final LocalDateTime paidAt;
    private final LocalDateTime createdAt;

    public PaymentResponse(Long paymentId, Long reservationId, BigDecimal amount, PaymentMethod paymentMethod, PaymentStatus status, LocalDateTime paidAt, LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }
}
