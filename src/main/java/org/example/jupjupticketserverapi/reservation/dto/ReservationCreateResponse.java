package org.example.jupjupticketserverapi.reservation.dto;

import lombok.Getter;
import org.example.jupjupticketserverapi.payment.dto.PaymentResponse;

@Getter
public class ReservationCreateResponse {

    private final ReservationResponse reservation;
    private final PaymentResponse payment;

    public ReservationCreateResponse(ReservationResponse reservation, PaymentResponse payment) {
        this.reservation = reservation;
        this.payment = payment;
    }
}
