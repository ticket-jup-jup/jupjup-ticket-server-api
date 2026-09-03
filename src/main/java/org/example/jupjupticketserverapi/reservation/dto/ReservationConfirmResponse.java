package org.example.jupjupticketserverapi.reservation.dto;

import lombok.Getter;
import org.example.jupjupticketserverapi.payment.dto.PaymentResponse;

@Getter
public class ReservationConfirmResponse {

    private final ReservationResponse reservation;
    private final PaymentResponse payment;

    public ReservationConfirmResponse(ReservationResponse reservation, PaymentResponse payment) {
        this.reservation = reservation;
        this.payment = payment;
    }
}
