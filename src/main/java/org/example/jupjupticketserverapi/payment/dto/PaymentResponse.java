package org.example.jupjupticketserverapi.payment.dto;

import lombok.Getter;

@Getter
public class PaymentResponse {

    private final Long paymentId;
    private final Long reservationId;
    private final Long amount;
    private final String status;

    public PaymentResponse(Long paymentId, Long reservationId, Long amount, String status) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.status = status;
    }
}
